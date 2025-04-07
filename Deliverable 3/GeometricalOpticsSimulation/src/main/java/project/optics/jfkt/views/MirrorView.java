package project.optics.jfkt.views;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import project.optics.jfkt.controllers.MirrorController;
import project.optics.jfkt.models.*;

import java.util.ArrayList;


public class MirrorView extends BaseView {
    private double centerX;
    private double centerY;
    private static final double DEFAULT_SCALE = 10; //pixels per cm
    private static final double DEFAULT_VELOCITY = 120; //pixels per second
    private double velocity = DEFAULT_VELOCITY;
    private double scale = DEFAULT_SCALE;
    private double objectDistance;
    private double objectHeight;
    private double focalLength;
    private boolean isConcave;
    private MirrorController mirrorController;
    private MirrorAnimation mirrorAnimation;

    public MirrorView() {
        super("Mirrors");
        mirrorController = new MirrorController(this);
        Pane animPane = getAnimpane();
        centerX = animPane.getPrefWidth()/2;
        centerY = animPane.getPrefHeight()/2;

        double defaultObjectDistance = objectDistance = 20.0;
        double defaultObjectHeight = objectHeight =  8.0;
        double defaultFocalLength = focalLength = 8.0;
        boolean defaultIsConcave = isConcave = true;

        ImageView concaveMirror = new ImageView(new Image(this.getClass().getResource("/images/concavemirror.png").toExternalForm()));
        ImageView convexMirror = new ImageView(new Image(this.getClass().getResource("/images/convexmirror.png").toExternalForm()));
        convexMirror.setFitHeight(160);
        convexMirror.setFitWidth(80);
        concaveMirror.setFitHeight(160);
        concaveMirror.setFitWidth(80);

        getOptionbutton1().setGraphic(concaveMirror);
        getOptionbutton2().setGraphic(convexMirror);
        getOptionbutton1().setOnAction(e -> mirrorController.onOption1ButtonPressed());
        getOptionbutton2().setOnAction(e -> mirrorController.onOption2ButtonPressed());

        getTextInputs().getFirst().setText(Double.toString(focalLength));
        getTextInputs().get(1).setText(Double.toString(objectDistance));
        getTextInputs().getLast().setText(Double.toString(objectHeight));
        getTextInputs().getFirst().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != "") {
                try {
                    double updatedFocalLength = Double.parseDouble(newValue);
                    mirrorController.onFocalLengthUpdated(updatedFocalLength);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a valid number").showAndWait();
                }
            }
        });
        getTextInputs().get(1).textProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue != "") {
                try {
                    double updatedObjectDistance = Double.parseDouble(newValue);
                    mirrorController.onObjectDistanceUpdated(updatedObjectDistance);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a valid number").showAndWait();
                }
            }
        });
        getTextInputs().getLast().textProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue != "") {
                try {
                    double updatedObjectHeight = Double.parseDouble(newValue);
                    mirrorController.onObjectHeightUpdated(updatedObjectHeight);
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter a valid number").showAndWait();
                }
            }
        });
        getPlaybutton().setOnAction(e->{
            mirrorController.onPlayButtonPressed();
        });
        getPausebutton().setOnAction(e->{
            mirrorController.onPauseButtonPressed();
        });
        getRedobutton().setOnAction(e->{
            mirrorController.onRedoButtonPressed();
        });
        getSlowbutton().setOnAction(e->{
            mirrorController.onSlowButtonPressed();
        });
        getNormalbutton().setOnAction(e->{
            mirrorController.onNormalButtonPressed();
        });
        getFastbutton().setOnAction(e->{
            mirrorController.onFastButtonPressed();
        });



        MirrorModel baseMirror = new MirrorModel(defaultFocalLength,centerX, centerY, scale, defaultIsConcave);
        ArrowModel baseArrowModel = new ArrowModel(defaultObjectHeight,scale,defaultObjectDistance,centerX,centerY);
        animPane.getChildren().addAll(baseMirror.getMirrorGroup(), baseArrowModel.getArrowObject());

        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,objectDistance,objectHeight,isConcave);
        ArrayList<CoordinateModel> firstCoordinateSet = mirrorCoordinateCalculations.getFirstCoordinateSet();
        ArrayList<CoordinateModel> secondCoordinateSet = mirrorCoordinateCalculations.getSecondCoordinateSet();
        ArrayList<CoordinateModel> thirdCoordinateSet = mirrorCoordinateCalculations.getThirdCoordinateSet();

        ArrowModel imageModel = new ArrowModel(mirrorCoordinateCalculations.getImageHeight(),scale,mirrorCoordinateCalculations.getImageDistance(),centerX,centerY);
        mirrorAnimation = new MirrorAnimation(velocity, firstCoordinateSet, secondCoordinateSet, thirdCoordinateSet, imageModel);
        getAnimpane().getChildren().add(mirrorAnimation);
    }


    public void updateView(){
        Pane animPane = getAnimpane();
        animPane.getChildren().clear();

        Line opticalAxis = new Line(0, animPane.getPrefHeight()/2, animPane.getPrefWidth(), animPane.getPrefHeight()/2);
        opticalAxis.setStroke(Color.BLACK);
        opticalAxis.setStrokeWidth(1);

        MirrorModel updatedMirror = new MirrorModel(focalLength, centerX, centerY, scale, isConcave);
        ArrowModel arrowModel = new ArrowModel(objectHeight,scale,objectDistance,centerX,centerY);
        animPane.getChildren().addAll(opticalAxis, updatedMirror.getMirrorGroup(), arrowModel.getArrowObject());

        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,objectDistance,objectHeight,isConcave);
        ArrayList<CoordinateModel> firstCoordinateSet = mirrorCoordinateCalculations.getFirstCoordinateSet();
        ArrayList<CoordinateModel> secondCoordinateSet = mirrorCoordinateCalculations.getSecondCoordinateSet();
        ArrayList<CoordinateModel> thirdCoordinateSet = mirrorCoordinateCalculations.getThirdCoordinateSet();

        ArrowModel imageModel = new ArrowModel(mirrorCoordinateCalculations.getImageHeight(),scale,mirrorCoordinateCalculations.getImageDistance(),centerX,centerY);
        mirrorAnimation = new MirrorAnimation(velocity, firstCoordinateSet, secondCoordinateSet, thirdCoordinateSet, imageModel);
        getAnimpane().getChildren().add(mirrorAnimation);
    }

    public void testCoordinate(){
        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,objectDistance,objectHeight,isConcave);
        ArrayList<CoordinateModel> firstCoordinateSet = mirrorCoordinateCalculations.getFirstCoordinateSet();
        ArrayList<CoordinateModel> secondCoordinateSet = mirrorCoordinateCalculations.getSecondCoordinateSet();
        ArrayList<CoordinateModel> thirdCoordinateSet = mirrorCoordinateCalculations.getThirdCoordinateSet();

        Circle firstCircle = new Circle(5);
        firstCircle.setFill(Color.BLUE);
        firstCircle.setStroke(Color.BLACK);
        firstCircle.setCenterX(firstCoordinateSet.get(1).getX());
        firstCircle.setCenterY(firstCoordinateSet.get(1).getY());
        System.out.println("Coordinate x " + (4) +" :" + secondCoordinateSet.get(1).getX());
        System.out.println("Coordinate y " + (4) +" :" + secondCoordinateSet.get(1).getY());

        Circle thirdCircle = new Circle(5);
        thirdCircle.setFill(Color.BLUE);
        thirdCircle.setStroke(Color.BLACK);
        thirdCircle.setCenterX(thirdCoordinateSet.get(1).getX());
        thirdCircle.setCenterY(thirdCoordinateSet.get(1).getY());
        System.out.println("Coordinate x " + (5) +" :" + thirdCoordinateSet.get(1).getX());
        System.out.println("Coordinate y " + (5) +" :" + thirdCoordinateSet.get(1).getY());

        getAnimpane().getChildren().addAll(firstCircle, thirdCircle);


        for (int i =0; i< 3; i++){
            Circle coordinate = new Circle(5);
            coordinate.setFill(Color.BLUE);
            coordinate.setStroke(Color.BLACK);
            coordinate.setCenterX(secondCoordinateSet.get(i).getX());
            coordinate.setCenterY(secondCoordinateSet.get(i).getY());
            System.out.println("Coordinate x " + (i+1) +" :" + secondCoordinateSet.get(i).getX());
            System.out.println("Coordinate y " + (i+1) +" :" + secondCoordinateSet.get(i).getY());
            getAnimpane().getChildren().add(coordinate);
        }

    }

    public void playAnim(){
        mirrorAnimation.animPlay();
    }

    public void pauseAnim(){
        mirrorAnimation.animPause();
    }


    public void setisConcave(Boolean isConcave){
        this.isConcave = isConcave;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public void setObjectDistance(double objectDistance){
        this.objectDistance = objectDistance;
    }

    public void setObjectHeight(double objectHeight){
        this.objectHeight = objectHeight;
    }

    public double getDefaultVelocity() {
        return DEFAULT_VELOCITY;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
