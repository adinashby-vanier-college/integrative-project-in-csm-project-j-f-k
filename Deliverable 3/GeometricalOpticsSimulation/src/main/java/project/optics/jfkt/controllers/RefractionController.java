package project.optics.jfkt.controllers;

import javafx.animation.PathTransition;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.views.LayerChoosingView;
import project.optics.jfkt.views.RefractionView;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RefractionController {
    public void onNewLayerButtonPressed(RefractionView refractionView, ArrayList<HBox> layers, VBox frame, int currentLayer, HBox plusSignLayer, Line line12, Line line23) {
        Stage stage = new Stage();
        stage.initOwner(MainApp.primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false); // Disable resizing
        stage.initStyle(StageStyle.UTILITY); // Hide minimize/maximize buttons
        stage.setTitle("Material selection");
        stage.setScene(new Scene(new LayerChoosingView(refractionView, stage, layers, frame, currentLayer, plusSignLayer, line12, line23)));
        stage.setFullScreen(false);
        stage.show();
    }

    public void onIncidentLocationChanged(Number incidentLocation, Circle incidentPoint) {
        incidentPoint.setCenterX(incidentLocation.doubleValue());
    }

    public void onPlayButtonPressed(RefractionView refractionView) {
        createAnimation(refractionView);



    }

    private void createAnimation(RefractionView refractionView) {
        // initial setup
        double angle = refractionView.getIncidentAngle();
        Circle object = refractionView.getIncidentPoint();
        Line path = new Line();
        path.setStartX(object.getCenterX());
        path.setStartY(object.getCenterY());
        // set stroke for debug
        path.setStroke(Color.BLACK);

        double y = refractionView.getLine12().getEndY();
        double xOffset = y * Math.tan(Math.toRadians(angle));

        path.setEndY(y);
        path.setEndX(object.getCenterX() + xOffset);

        PathTransition animation = new PathTransition();
        animation.setNode(object);
        animation.setPath(path);
        animation.setCycleCount(1);
        animation.setAutoReverse(false);

        animation.play();

        // detect the change of layer
        AtomicBoolean isGoingUp = new AtomicBoolean(false);

        object.translateYProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < oldVal.doubleValue()) {
                isGoingUp.set(true); // Update the value via set()
            } else if (newVal.doubleValue() > oldVal.doubleValue()) {
                isGoingUp.set(false);
            }
        });

        int currentLayer = refractionView.getCurrentLayer();
        double line12y = refractionView.getLine12().getEndY();
        double line23y = refractionView.getLine23().getEndY();

        if (currentLayer == 2) {
            while (isInTheRange(object, refractionView)) {
                if (isGoingUp.get()) {

                }
            }
        }

        if (currentLayer == 3) {

        }


        animation.stop();
    }

    private boolean isInTheRange(Circle object, RefractionView refractionView) {
        VBox restrictionZone = refractionView.getFrame();
        int currentLayer = refractionView.getCurrentLayer();

        if (currentLayer == 2) {
            double twoThirdsHeight = (2.0 / 3) * restrictionZone.getHeight();

            if (object.getCenterY() > twoThirdsHeight) {
                return false; // Out of valid range
            }

            if (object.getCenterY() < 0) {
                return false;
            }

            return true; // Within valid range
        }

        if (currentLayer == 3) {
            if (object.getCenterY() > restrictionZone.getHeight()) {
                return false;
            }

            if (object.getCenterY() < 0) {
                return false;
            }

            return true;
        }

        System.out.println("Bug somewhere, because impossible to reach this statement");
        return true;
    }


}
