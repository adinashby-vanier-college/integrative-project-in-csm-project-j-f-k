package project.optics.jfkt.views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import project.optics.jfkt.enums.Material;
import project.optics.jfkt.models.Refraction;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;

public class RefractionView extends VBox {
    private Refraction refraction = new Refraction();
    private final RefractionController refractionController = new RefractionController(refraction, this);
    private final Util util = new Util();
    private static final double ANIMATION_PANE_HEIGHT = 900;
    private Material chosenLayer;
    private SimpleDoubleProperty incidentLocation = new SimpleDoubleProperty(0);
    private Circle object;
    private SimpleDoubleProperty incidentAngle = new SimpleDoubleProperty(45);
    private VBox layerPane;
    private ArrayList<HBox> layers;
    private StackPane animationPane;
    private Pane trailPane;
    private Rectangle rectangleClip;

    public RefractionView() {
        Region menu = util.createMenu();
        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(50);
        VBox.setMargin(topButtons, new Insets(10, 0, 0, 0));
        this.getChildren().addAll(menu, topButtons, createAnimationPane(), createBottom());

        // resize the clipping area
        refraction.layerCountProperty().addListener((observable, oldValue, newValue) -> refractionController.onLayerCountChanged(newValue.intValue()));
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
        // The outerPane including the layers and the trail of animation
        StackPane animationPane = new StackPane();
        this.animationPane = animationPane;

        // The pane displaying the trail and the object of animation
        Pane trailPane = new Pane();
        trailPane.setMouseTransparent(true);
        animationPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                trailPane.setPrefHeight(newValue.doubleValue());
            }
        });
        animationPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                trailPane.setPrefWidth(newValue.doubleValue());
            }
        });

        this.trailPane = trailPane;

        // initialize the pane
        VBox layerPane = new VBox();
        layerPane.setPrefHeight(ANIMATION_PANE_HEIGHT);
        layerPane.setBorder(Border.stroke(Color.BLACK));
        this.layerPane = layerPane;

        animationPane.getChildren().addAll(layerPane, trailPane);
        trailPane.toFront();

        // create the object of animation
        object = new Circle(3);
        object.setStroke(Color.BLUE);
        object.setFill(Color.BLUE);
        object.setManaged(false);

        // object's X position dynamically change with the incident location slider
        incidentLocation.addListener((observable, oldValue, newValue) -> refractionController.onIncidentLocationChanged(newValue, object));

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
                "-fx-pref-width: 0; -fx-pref-height: 0; " + // Zero preferred size
                "-fx-min-width: 0; -fx-min-height: 0; " );

        HBox plusSignLayer = new HBox(newLayer);
        plusSignLayer.setAlignment(Pos.CENTER);

        VBox.setVgrow(plusSignLayer, Priority.ALWAYS);

        layerPane.getChildren().add(plusSignLayer);

        newLayer.setOnAction(event -> refractionController.onNewLayerButtonPressed(RefractionView.this, layers, layerPane, plusSignLayer));

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
        Region pausePlayAndRestartButtons = createPausePlayAndRestartButtons();

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

    private Region createPausePlayAndRestartButtons() {
        Image pauseImg = new Image(this.getClass().getResource("/images/64/Pause.png").toExternalForm());
        ImageView pauseImgView = new ImageView(pauseImg);
        Button pause = new Button();
        pause.setGraphic(pauseImgView);

        Image playImg = new Image(this.getClass().getResource("/images/64/Play.png").toExternalForm());
        ImageView playImgView = new ImageView(playImg);
        Button play = new Button();
        play.setGraphic(playImgView);
        play.setOnAction(event -> refractionController.onPlayButtonPressed());

        Image restartImg = new Image(this.getClass().getResource("/images/64/Redo.png").toExternalForm());
        ImageView restartImgView = new ImageView(restartImg);
        Button restart = new Button();
        restart.setGraphic(restartImgView);

        HBox container = new HBox(20, pause, play, restart);

        return container;
    }

    private Region createAnimationSpeedButtons() {
        RadioButton slow = new RadioButton("Slow");
        RadioButton normal = new RadioButton("Normal");
        RadioButton fast = new RadioButton("Fast");
        normal.setSelected(true);

        ToggleGroup toggleGroup = new ToggleGroup();

        slow.setToggleGroup(toggleGroup);
        normal.setToggleGroup(toggleGroup);
        fast.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> refractionController.onAnimationSpeedChanged(newVal));

        HBox container = new HBox(20, slow, normal, fast);

        return container;
    }

    private Region createParameters() {
        GridPane parameters = new GridPane(5, 10);

        Label parameterLbl = new Label("Parameters");
        parameters.add(parameterLbl, 0, 0, 2, 1);

        Label angleLbl = new Label("Incident angle (to vertical)");
        parameters.add(angleLbl, 0, 1);

        Label incidentPointLbl = new Label("Incident location");
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

        // update incident angle with the angle slider dynamically
        angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> incidentAngle.set(newValue.doubleValue()));

        Text angleValue = new Text();
        angleValue.setWrappingWidth(45);

        StringConverter<Number> converter = new NumberStringConverter();

        Bindings.bindBidirectional(angleValue.textProperty(), angleSlider.valueProperty(), converter);

        HBox container = new HBox(10, angleSlider, angleValue);

        return container;
    }

    private Region createLocationComponent() {
        Slider locationSlider = new Slider();
        locationSlider.setValue(0);
        locationSlider.setMajorTickUnit(15);
        locationSlider.setMinorTickCount(5);
        locationSlider.setSnapToTicks(true);

        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            locationSlider.setMax(newVal.doubleValue());
        });
        locationSlider.setBlockIncrement(1);
        locationSlider.setMin(0);
        locationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                incidentLocation.set(newValue.doubleValue());
            }
        });

        Text locationValue = new Text();
        locationValue.setWrappingWidth(60);

        StringConverter<Number> converter = new NumberStringConverter();

        Bindings.bindBidirectional(locationValue.textProperty(), locationSlider.valueProperty(), converter);

        HBox container = new HBox(10, locationSlider, locationValue);

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
}
