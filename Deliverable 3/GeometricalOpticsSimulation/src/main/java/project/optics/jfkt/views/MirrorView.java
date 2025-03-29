package project.optics.jfkt.views;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import project.optics.jfkt.controllers.MirrorController;
import project.optics.jfkt.models.MirrorModel;


public class MirrorView extends BaseView {
    private double centerX;
    private double centerY;
    private static final double DEFAULT_SCALE = 10; //pixels per cm
    private double scale = DEFAULT_SCALE;
    private double objectDistance;
    private double objectHeight;
    private double focalLength;
    private boolean isConcave;
    private MirrorController mirrorController;

    public MirrorView() {
        super("Mirrors");
        mirrorController = new MirrorController(this);
        Pane animPane = getAnimpane();
        centerX = animPane.getPrefWidth()/2;
        centerY = animPane.getPrefHeight()/2;
        //place base mirror & Objects
        double defaultObjectDistance = objectDistance = 8.0;
        double defaultObjectHeight = objectHeight =  2.0;
        double defaultFocalLength = focalLength = 8.0;
        boolean defaultIsConcave = isConcave = true;

        ImageView concaveMirror = new ImageView(new Image(this.getClass().getResource("/images/convexmirror.png").toExternalForm()));
        ImageView convexMirror = new ImageView(new Image(this.getClass().getResource("/images/concavemirror.png").toExternalForm()));
        convexMirror.setFitHeight(160);
        convexMirror.setFitWidth(80);
        concaveMirror.setFitHeight(160);
        concaveMirror.setFitWidth(80);

        getOptionbutton1().setGraphic(convexMirror);
        getOptionbutton2().setGraphic(concaveMirror);
        getOptionbutton1().setOnAction(e -> mirrorController.onOption1ButtonPressed());
        getOptionbutton2().setOnAction(e -> mirrorController.onOption2ButtonPressed());

        getTextInputs().getFirst().setText(Double.toString(focalLength));
        getTextInputs().get(1).setText(Double.toString(objectDistance));
        getTextInputs().getLast().setText(Double.toString(objectHeight));
        getTextInputs().getFirst().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != "") {
                try {
                    double focalLength = Double.parseDouble(newValue);
                    mirrorController.onFocalLengthUpdated(focalLength);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a valid number").showAndWait();
                }
            }
        });


        MirrorModel baseMirror = new MirrorModel(defaultFocalLength,centerX, centerY, scale, defaultIsConcave);
        animPane.getChildren().add(baseMirror.getMirrorGroup());
    }

    void createObject(){

    }

    public void updateView(){
        Pane animPane = getAnimpane();
        animPane.getChildren().clear();

        Line opticalAxis = new Line(0, animPane.getPrefHeight()/2, animPane.getPrefWidth(), animPane.getPrefHeight()/2);
        opticalAxis.setStroke(Color.BLACK);
        opticalAxis.setStrokeWidth(1);

        MirrorModel updatedMirror = new MirrorModel(focalLength, centerX, centerY, scale, isConcave);
        animPane.getChildren().addAll(opticalAxis, updatedMirror.getMirrorGroup());
    }

    public void setisConcave(Boolean isConcave){
        this.isConcave = isConcave;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }
}
