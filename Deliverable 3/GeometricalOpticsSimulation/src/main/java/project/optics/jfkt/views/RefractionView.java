package project.optics.jfkt.views;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import project.optics.jfkt.controllers.RefractionController;
import project.optics.jfkt.enums.Material;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;

public class RefractionView extends VBox {
    private final RefractionController refractionController = new RefractionController();
    private final Util util = new Util();
    private double layerLength;
    private static final double ANIMATION_PANE_HEIGHT = 900;
    private int currentLayer = 1;
    private Material chosenLayer;
    private double currentRefractionIndex;

    public RefractionView() {
        layerLength = ANIMATION_PANE_HEIGHT;
        Region menu = util.createMenu();
        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(50);
        VBox.setMargin(topButtons, new Insets(10, 0, 0, 0));

        this.getChildren().addAll(menu, topButtons, createAnimationPane(), createBottom());
    }

    private VBox createAnimationPane() {
        VBox frame = new VBox();
        frame.setPrefHeight(ANIMATION_PANE_HEIGHT);
        frame.setBorder(Border.stroke(Color.BLACK));

        ArrayList<HBox> layers = new ArrayList<>();

        HBox layer1 = new HBox();
        layer1.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer1, Priority.ALWAYS);

        HBox layer2 = new HBox();
        layer1.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer2, Priority.ALWAYS);

        HBox layer3 = new HBox();
        layer1.setBorder(Border.stroke(Color.BLACK));
        VBox.setVgrow(layer3, Priority.ALWAYS);

        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);

        Button newLayer = new Button();
        newLayer.setGraphic(new StackPane(drawPlusSign()));
        newLayer.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand;");


        HBox plusSignLayer = new HBox(newLayer);
        plusSignLayer.setAlignment(Pos.CENTER);

        VBox.setVgrow(plusSignLayer, Priority.ALWAYS);

        frame.getChildren().add(plusSignLayer);

        newLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (currentLayer < 4) {
                    refractionController.onNewLayerButtonPressed(RefractionView.this, layers, frame, currentLayer, plusSignLayer);
                }

                currentLayer++;
            }
        });

        return frame;
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

        Image restartImg = new Image(this.getClass().getResource("/images/64/Redo.png").toExternalForm());
        ImageView restartImgView = new ImageView(restartImg);
        Button restart = new Button();
        restart.setGraphic(restartImgView);

        HBox container = new HBox(20, pause, play, restart);

        return container;
    }

    private Region createAnimationSpeedButtons() {
        Button slow = new Button("Slow");
        Button normal = new Button("Normal");
        Button fast = new Button("Fast");

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

        Text angleValue = new Text();

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

        Text locationValue = new Text();

        StringConverter<Number> converter = new NumberStringConverter();

        Bindings.bindBidirectional(locationValue.textProperty(), locationSlider.valueProperty(), converter);

        HBox container = new HBox(10, locationSlider, locationValue);

        return container;
    }

    public void setChosenLayer(Material chosenLayer) {
        this.chosenLayer = chosenLayer;
    }

    public void setCurrentRefractionIndex(double currentRefractionIndex) {
        this.currentRefractionIndex = currentRefractionIndex;
    }
}
