package project.optics.jfkt.views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import project.optics.jfkt.controllers.RefractionController;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.enums.AnimationStatus;
import project.optics.jfkt.enums.Material;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.models.Refraction;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;

public class RefractionView extends Pane {
    private final Util util = new Util();
    private final Refraction refraction = new Refraction();
    private final RefractionController refractionController = new RefractionController(refraction, this);
    private static final double ANIMATION_PANE_HEIGHT = 830;
    private Material chosenLayer;
    private SimpleDoubleProperty incidentLocation = new SimpleDoubleProperty(0);
    private Circle object;
    private SimpleDoubleProperty incidentAngle = new SimpleDoubleProperty(45);
    private VBox layerPane;
    private ArrayList<HBox> layers;
    private StackPane animationPane;
    private Pane trailPane;
    private Rectangle rectangleClip;
    private Slider locationSlider;
    private Slider angleSlider;
    private Button newLayer;
    private SimpleObjectProperty<AnimationStatus> animationStatusProperty = new SimpleObjectProperty<>(AnimationStatus.PREPARED);
    private ToggleGroup animationSpeed;
    private Label parameterLbl;
    private Label angleLbl;
    private Label incidentPointLbl;


    public RefractionView() {
        // add all components to the scene
        VBox vBox = new VBox(createMenu(), createTopButtons(), createAnimationPane(), createBottom());
        vBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        vBox.maxWidthProperty().bind(this.widthProperty());
        vBox.maxHeightProperty().bind(this.heightProperty());
        this.getChildren().addAll(vBox, createProtractor());

        // Add listener to ensure proper layout
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            vBox.setPrefWidth(newVal.doubleValue());
            vBox.requestLayout();
        });

        this.heightProperty().addListener((obs, oldVal, newVal) -> {
            vBox.setPrefHeight(newVal.doubleValue());
            vBox.requestLayout();
        });

        // resize the clipping area
        refraction.layerCountProperty().addListener((observable, oldValue, newValue) -> refractionController.onLayerCountChanged(newValue.intValue()));

        // Disable user inputs when the animation status is modified
        animationStatusProperty.addListener((observable, oldValue, newValue) -> refractionController.onAnimationStatusChanged(newValue, locationSlider, angleSlider, newLayer));

        // add audio
        refractionController.addAudioWhenStartAndFinish();
        // Add font change listener
        ThemeController.addFontChangeListener(this::updateFont);

        // Apply current font initially
        updateFont(ThemeController.getCurrentFont());
    }
    private void updateFont(String font) {
        // Apply font to all text elements in the refraction view
        String fontStyle = "-fx-font-family: '" + font + "';";
        this.setStyle(fontStyle);

        // Apply to specific components that might need it
        if (angleSlider != null) {
            angleSlider.setStyle(fontStyle);
        }
        if (locationSlider != null) {
            locationSlider.setStyle(fontStyle);
        }
        if (animationSpeed != null) {
            animationSpeed.getToggles().forEach(toggle -> {
                RadioButton radio = (RadioButton) toggle;
                radio.setStyle(fontStyle);
            });
        }

        // Apply to parameter labels
        if (parameterLbl != null) parameterLbl.setStyle(fontStyle);
        if (angleLbl != null) angleLbl.setStyle(fontStyle);
        if (incidentPointLbl != null) incidentPointLbl.setStyle(fontStyle);

        // Force a CSS refresh
        if (this.getScene() != null) {
            this.getScene().getRoot().applyCss();
        }
    }


    private Region createMenu() {
        return util.createMenu();
    }

    private Region createTopButtons() {
        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(100);
        VBox.setMargin(topButtons, new Insets(10, 0, 0, 0));
        return topButtons;
    }

    private Group createProtractor() {
        Group protractor = util.createProtractor();
        protractor.setLayoutX(400);
        protractor.setLayoutY(20);
        return protractor;
    }

    private void initializeLayers() {
        layers = new ArrayList<>();

        HBox layer1 = new HBox();
        layer1.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer1, Priority.ALWAYS);

        HBox layer2 = new HBox();
        layer2.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer2, Priority.ALWAYS);

        HBox layer3 = new HBox();
        layer3.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer3, Priority.ALWAYS);

        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
    }

    private StackPane createAnimationPane() {
        StackPane animationPane = new StackPane();
        animationPane.getStyleClass().addAll("refraction-animation-pane", "refraction-text");
        this.animationPane = animationPane;

        Pane trailPane = new Pane();
        trailPane.setMouseTransparent(true);
        trailPane.getStyleClass().add("refraction-text");
        animationPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            trailPane.setPrefHeight(newValue.doubleValue());
        });
        animationPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            trailPane.setPrefWidth(newValue.doubleValue());
        });
        this.trailPane = trailPane;

        VBox layerPane = new VBox();
        layerPane.setPrefHeight(ANIMATION_PANE_HEIGHT);
        layerPane.setBorder(Border.stroke(Color.BLACK));
        layerPane.getStyleClass().addAll("refraction-outline", "refraction-text");
        this.layerPane = layerPane;

        animationPane.getChildren().addAll(layerPane, trailPane);
        trailPane.toFront();


        // create the object of animation
        object = new Circle(5);
        object.setStroke(Color.BLACK);
        object.setFill(Color.BLACK);
        object.setManaged(false);
        object.setVisible(false);

        // the object would be displayed only when there are two or more layers
        refraction.layerCountProperty().addListener((obs, oldVal, newVal) -> {
            object.setVisible(newVal.intValue() >= 2);
        });

        // object's X position dynamically change with the incident location slider
        incidentLocation.addListener((observable, oldValue, newValue) ->
                refractionController.onIncidentLocationChanged(newValue, object));

        // initialization center Y value of incident point
        object.setTranslateY(0);
        trailPane.getChildren().add(object);

        // hide overflowed elements of the pane
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(animationPane.widthProperty());
        clip.heightProperty().bind(animationPane.heightProperty());
        this.rectangleClip = clip;
        trailPane.setClip(clip);

        // create layers
        initializeLayers();

        // plus sign set up
        Button newLayer = new Button();
        StackPane plusGraphic = new StackPane(drawPlusSign());
        plusGraphic.setPrefSize(0, 0);
        newLayer.setGraphic(plusGraphic);
        newLayer.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-pref-width: 0; -fx-pref-height: 0; " +
                "-fx-min-width: 0; -fx-min-height: 0; ");

        this.newLayer = newLayer;

        HBox plusSignLayer = new HBox(newLayer);
        plusSignLayer.setAlignment(Pos.CENTER);
        VBox.setVgrow(plusSignLayer, Priority.ALWAYS);
        layerPane.getChildren().add(plusSignLayer);

        newLayer.setOnAction(event ->
                refractionController.onNewLayerButtonPressed(RefractionView.this, layers, layerPane, plusSignLayer));

        return animationPane;
    }

    public SVGPath drawPlusSign() {
        SVGPath cross = new SVGPath();

        cross.setContent("M -8,0 H 8 V 16 H 12 V 0 H 28 V -4 H 12 V -20 H 8 V -4 H -8 V 0 Z");

        cross.setFill(Color.WHITE);
        cross.setStroke(Color.BLACK);
        cross.setStrokeWidth(1);

        cross.setScaleX(2);
        cross.setScaleY(2);

        return cross;
    }

    private Region createBottom() {
        HBox container = new HBox();

        Region parameters = createParameters();
        parameters.setPrefWidth(340);
        Region animationSpeedButtons = createAnimationSpeedButtons();
        Region pausePlayAndRestartButtons = createPausePlayAndRefreshButtons();

        Region spacer1 = new Region();
        Region spacer2 = new Region();

        container.getChildren().addAll(parameters, spacer1, animationSpeedButtons, spacer2, pausePlayAndRestartButtons);

        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox.setMargin(parameters, new Insets(10));
        HBox.setMargin(animationSpeedButtons, new Insets(25, 0, 0, 0));
        HBox.setMargin(pausePlayAndRestartButtons, new Insets(25, 10, 0, 0));

        return container;
    }

    private Region createPausePlayAndRefreshButtons() {
        Image pauseImg = new Image(this.getClass().getResource("/images/64/Pause.png").toExternalForm());
        ImageView pauseImgView = new ImageView(pauseImg);
        Button pause = new Button();
        pause.setGraphic(pauseImgView);

        Image playImg = new Image(this.getClass().getResource("/images/64/Play.png").toExternalForm());
        ImageView playImgView = new ImageView(playImg);
        Button play = new Button();
        play.setGraphic(playImgView);

        Image refreshImg = new Image(this.getClass().getResource("/images/64/Redo.png").toExternalForm());
        ImageView restartImgView = new ImageView(refreshImg);
        Button refresh = new Button();
        refresh.setGraphic(restartImgView);

        play.setOnAction(event -> refractionController.onPlayButtonPressed());
        pause.setOnAction(event -> refractionController.onPausePressed());
        refresh.setOnAction(event -> refractionController.onRefreshButtonPressed());

        HBox container = new HBox(20, pause, play, refresh);

        return container;
    }

    private Region createAnimationSpeedButtons() {
        RadioButton slow = new RadioButton(GeneralSetting.getString("button.slow"));
        RadioButton normal = new RadioButton(GeneralSetting.getString("button.normal"));
        RadioButton fast = new RadioButton(GeneralSetting.getString("button.fast"));
        normal.setSelected(true);

        slow.getStyleClass().addAll("refraction-text", "refraction-radio-text");
        normal.getStyleClass().addAll("refraction-text", "refraction-radio-text");
        fast.getStyleClass().addAll("refraction-text", "refraction-radio-text");

        ToggleGroup toggleGroup = new ToggleGroup();
        slow.setToggleGroup(toggleGroup);
        normal.setToggleGroup(toggleGroup);
        fast.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
                refractionController.onAnimationSpeedChanged(newVal));

        animationSpeed = toggleGroup;

        HBox container = new HBox(20, slow, normal, fast);
        container.getStyleClass().addAll("refraction-text", "refraction-radio-container");

        return container;
    }

    private Region createParameters() {
        GridPane parameters = new GridPane(5, 10);
        parameters.getStyleClass().addAll("refraction-parameters", "refraction-text");

        parameterLbl = new Label(GeneralSetting.getString("text.parameters"));
        parameterLbl.getStyleClass().addAll("refraction-header", "refraction-parameters-header", "parameter-label");
        parameters.add(parameterLbl, 0, 0, 2, 1);

        angleLbl = new Label(GeneralSetting.getString("label.incidentAngle"));
        angleLbl.getStyleClass().addAll("refraction-label", "refraction-parameters-label", "parameter-label");
        parameters.add(angleLbl, 0, 1);

        incidentPointLbl = new Label(GeneralSetting.getString("label.incidentLocation"));
        incidentPointLbl.getStyleClass().addAll("refraction-label", "refraction-parameters-label", "parameter-label");
        parameters.add(incidentPointLbl, 0, 2);

        parameters.add(createAngleComponent(), 1, 1, 2, 1);
        parameters.add(createLocationComponent(), 1, 2, 2, 1);

        return parameters;
    }

    private Region createAngleComponent() {
        Slider angleSlider = new Slider(0, 90, 45);
        angleSlider.setMajorTickUnit(15);
        angleSlider.setMinorTickCount(5);
        angleSlider.setSnapToTicks(true);
        angleSlider.setBlockIncrement(0.5);
        angleSlider.getStyleClass().add("refraction-text");

        angleSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                refractionController.onInitialAngleChanged(newValue.doubleValue(), locationSlider.getValue()));

        Text angleValue = new Text();
        angleValue.setWrappingWidth(45);
        angleValue.getStyleClass().add("refraction-value");

        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(angleValue.textProperty(), angleSlider.valueProperty(), converter);

        HBox container = new HBox(10, angleSlider, angleValue);
        container.getStyleClass().add("refraction-text");
        this.angleSlider = angleSlider;

        return container;
    }

    private Region createLocationComponent() {
        Slider locationSlider = new Slider();
        locationSlider.setValue(0);
        locationSlider.setMajorTickUnit(15);
        locationSlider.setMinorTickCount(5);
        locationSlider.setSnapToTicks(true);
        locationSlider.getStyleClass().add("refraction-text");

        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            locationSlider.setMax(newVal.doubleValue());
        });
        locationSlider.setBlockIncrement(1);
        locationSlider.setMin(0);
        locationSlider.valueProperty().addListener((observable, oldVal, newVal) ->
                refractionController.onInitialLocationChanged(newVal.doubleValue()));

        Text locationValue = new Text();
        locationValue.setWrappingWidth(60);
        locationValue.getStyleClass().add("refraction-value");

        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(locationValue.textProperty(), locationSlider.valueProperty(), converter);

        HBox container = new HBox(10, locationSlider, locationValue);
        container.getStyleClass().add("refraction-text");
        this.locationSlider = locationSlider;

        return container;
    }


    public void setChosenLayer(Material chosenLayer) {
        this.chosenLayer = chosenLayer;
    }

    public Circle getObject() {
        return object;
    }

    public double getIncidentAngle() {
        return incidentAngle.get();
    }

    public SimpleDoubleProperty incidentAngleProperty() {
        return incidentAngle;
    }

    public VBox getlayerPane() {
        return layerPane;
    }

    public Material getChosenLayer() {
        return chosenLayer;
    }

    public ArrayList<HBox> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<HBox> layers) {
        this.layers = layers;
    }

    public double getIncidentLocation() {
        return incidentLocation.get();
    }

    public SimpleDoubleProperty incidentLocationProperty() {
        return incidentLocation;
    }

    public StackPane getAnimationPane() {
        return animationPane;
    }

    public void setAnimationPane(StackPane animationPane) {
        this.animationPane = animationPane;
    }

    public Pane getTrailPane() {
        return trailPane;
    }

    public void setTrailPane(Pane trailPane) {
        this.trailPane = trailPane;
    }

    public Rectangle getRectangleClip() {
        return rectangleClip;
    }

    public void setRectangleClip(Rectangle rectangleClip) {
        this.rectangleClip = rectangleClip;
    }

    public AnimationStatus getAnimationStatus() {
        return animationStatusProperty.get();
    }

    public void setAnimationStatus(AnimationStatus animationStatus) {
        animationStatusProperty.set(animationStatus);
    }

    public SimpleObjectProperty<AnimationStatus> animationStatusProperty() {
        return animationStatusProperty;
    }

    public void setIncidentLocation(double incidentLocation) {
        this.incidentLocation.set(incidentLocation);
    }

    public void setIncidentAngle(double incidentAngle) {
        this.incidentAngle.set(incidentAngle);
    }

    public String getAnimationSpeed() {
        RadioButton selectedButton = (RadioButton) animationSpeed.getSelectedToggle();
        String selectedTxt = selectedButton.getText();

        return selectedTxt;
    }
}
