package project.optics.jfkt.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.models.Refraction;
import project.optics.jfkt.views.LayerChoosingView;
import project.optics.jfkt.views.RefractionView;

import java.util.ArrayList;
import java.util.List;

public class RefractionController {
    private Refraction refraction;
    private RefractionView refractionView;
    public RefractionController(Refraction refraction, RefractionView refractionView) {
        this.refraction = refraction;
        this.refractionView = refractionView;
    }

    public void onNewLayerButtonPressed(RefractionView refractionView, ArrayList<HBox> layers, VBox frame, int currentLayer, HBox plusSignLayer) {
        if (refractionView.getCurrentLayer() < 4) {
            Stage stage = new Stage();
            stage.initOwner(MainApp.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false); // Disable resizing
            stage.initStyle(StageStyle.UTILITY); // Hide minimize/maximize buttons
            stage.setTitle("Material selection");
            stage.setScene(new Scene(new LayerChoosingView(refractionView, stage, layers, frame, currentLayer, plusSignLayer)));
            stage.setFullScreen(false);
            stage.show();
        }

        refractionView.setCurrentLayer(refractionView.getCurrentLayer() + 1);
    }

    public void onIncidentLocationChanged(Number incidentLocation, Circle incidentPoint) {
        incidentPoint.setCenterX(incidentLocation.doubleValue());
    }

    public void onPlayButtonPressed() {
        boolean flag = update();
        List<Point2D> path;
        Timeline animation;

        if (flag) { // if update successfully: at least two layers were settled
            if (refraction.getLayerCount() == 3) {
                path = calculate3();
            } else if (refraction.getLayerCount() == 2) {
                path = calculate2(0); // start at y = 0
            } else {
                // Handle other cases or assign a default value.
                throw new IllegalStateException("Unsupported layer count: " + refraction.getLayerCount());
            }

            animation = createAnimation(path, Duration.seconds(4));
            animation.play();
        }
    }

    private boolean update() {
        ArrayList<HBox> layers = refractionView.getLayers();
        HBox layer1 = layers.get(0);
        HBox layer2 = layers.get(1);
        HBox layer3 = layers.get(2);

        if (layer2.getBackground() == null) {
            return false;
        }

        if (layer3.getBackground() == null) {
            refraction.setLayerCount(2);
            refraction.setLayer1(layer1);
            refraction.setLayer2(layer2);
            refraction.setN1(convertLayerToRefractionIndex(layer1));
            refraction.setN2(convertLayerToRefractionIndex(layer2));
            refraction.setInitialAngle(refractionView.getIncidentAngle());
            refraction.setInitialLocation(refractionView.getIncidentLocation());
        } else {
            refraction.setLayerCount(3);
            refraction.setLayer1(layer1);
            refraction.setLayer2(layer2);
            refraction.setLayer3(layer3);
            refraction.setN1(convertLayerToRefractionIndex(layer1));
            refraction.setN2(convertLayerToRefractionIndex(layer2));
            refraction.setN3(convertLayerToRefractionIndex(layer3));
            refraction.setInitialAngle(refractionView.getIncidentAngle());
            refraction.setInitialLocation(refractionView.getIncidentLocation());
        }

        return true;
    }

    private List<Point2D> calculate3() {
        List<Point2D> path = new ArrayList<>();

        // Starting conditions: initial position and incident angle (degrees relative to vertical)
        // A positive angle means the ray’s horizontal component is to the right; negative means to the left.
        double incidentAngleRad = Math.toRadians(refraction.getInitialAngle());
        double currentX = refraction.getInitialLocation();
        double currentY = 0;
        path.add(new Point2D(currentX, currentY)); // starting point

        // Retrieve layers, their refraction indices and their heights
        HBox layer1 = refraction.getLayer1();
        HBox layer2 = refraction.getLayer2();
        HBox layer3 = refraction.getLayer3();
        double n1 = refraction.getN1();
        double n2 = refraction.getN2();
        double n3 = refraction.getN3();
        double h1 = layer1.getHeight();
        double h2 = layer2.getHeight();
        double h3 = layer3.getHeight();

        // ------------------------------
        // Interface 1: Transition from layer1 to layer2
        // ------------------------------
        // Calculate the intersection point at the bottom of layer1.
        double dx = h1 * Math.tan(incidentAngleRad);
        double nextX = currentX + dx;
        double nextY = currentY + h1;
        path.add(new Point2D(nextX, nextY)); // add interface point

        // Determine the new angle for transition from layer1 to layer2 using Snell's law.
        double sinIncident = Math.sin(incidentAngleRad);
        double sinRefracted = (n1 / n2) * sinIncident;
        if (Math.abs(sinRefracted) > 1) {
            // Total internal reflection at interface 1:
            double reflectedX = nextX + dx; // reflected, so plus dx
            double reflectedY = currentY; // reflected back to the initial height

            path.add(new Point2D(reflectedX, reflectedY));
            return path;
        }

        // Update for next layer.
        currentX = nextX;
        currentY = nextY;
        incidentAngleRad = Math.asin(sinRefracted);

        // ------------------------------
        // Interface 2: Transition from layer2 to layer3
        // ------------------------------
        dx = h2 * Math.tan(incidentAngleRad);
        nextX = currentX + dx;
        nextY = currentY + h2;
        path.add(new Point2D(nextX, nextY)); // add interface point

        sinIncident = Math.sin(incidentAngleRad);
        sinRefracted = (n2 / n3) * sinIncident;
        if (Math.abs(sinRefracted) > 1) {
            // TIR occurs, first reflected to the interface between layer1 and layer2
            double reflectedX = nextX + dx;
            double reflectedY = currentY;
            path.add(new Point2D(reflectedX, reflectedY));

            // Then, refracted back to y = 0;
            double refractedX = nextX * 2; // just double the midpoint's x because it's symmetric
            double refractedY = 0;
            path.add(new Point2D(refractedX, refractedY));

            return path;
        }

        // Update for layer3.
        currentX = nextX;
        currentY = nextY;
        incidentAngleRad = Math.asin(sinRefracted);

        // ------------------------------
        // Exit from layer3: the bottom of the third layer.
        // ------------------------------
        dx = h3 * Math.tan(incidentAngleRad);
        double exitX = currentX + dx;
        double exitY = currentY + h3;
        path.add(new Point2D(exitX, exitY)); // final exit point

        return path;
    }

    /**
     *
     * @return a list of points that the object goes through (path of animation) when there are two layers
     */
    private List<Point2D> calculate2(double currentY) {
        List<Point2D> path = new ArrayList<>();

        // Convert the incident angle from degrees (relative to the vertical) to radians.
        double incidentAngleRad = Math.toRadians(refraction.getInitialAngle());
        double currentX = refraction.getInitialLocation();

        // Get the two layers.
        HBox layer1 = refraction.getLayer1();
        HBox layer2 = refraction.getLayer2();
        double layerHeight1 = layer1.getHeight();
        double layerHeight2 = layer2.getHeight();
        double n1 = refraction.getN1();
        double n2 = refraction.getN2();

        // Add the starting point.
        path.add(new Point2D(currentX, currentY));

        // --- In the first layer ---
        // Calculate the horizontal displacement in layer1.
        double dx1 = layerHeight1 * Math.tan(incidentAngleRad);
        double nextX = currentX + dx1;
        double nextY = currentY + layerHeight1;

        // Check for total internal reflection at the interface between layer1 and layer2.
        double sinIncident = Math.sin(incidentAngleRad);
        double sinRefracted = (n1 / n2) * sinIncident;
        if (Math.abs(sinRefracted) > 1) {
            // Total internal reflection occurs.
            // 1. Add the point where the ray hits the interface.
            path.add(new Point2D(nextX, nextY));
            // 2. Reflect the ray: for a horizontal interface, reflection reverses the horizontal displacement.
            double reflectedX = nextX + dx1; // returns to original x at the top of layer1.
            double reflectedY = currentY;     // back at the top.
            path.add(new Point2D(reflectedX, reflectedY));
            // End simulation here.
            return path;
        }

        // No total internal reflection: normal refraction into layer2.
        // Add the exit point from layer1.
        path.add(new Point2D(nextX, nextY));

        // Update the incident angle using Snell's law for the transition from layer1 to layer2.
        incidentAngleRad = Math.asin(sinRefracted);

        // --- In the second layer ---
        // Compute the displacement in layer2.
        double dx2 = layerHeight2 * Math.tan(incidentAngleRad);
        double finalX = nextX + dx2;
        double finalY = nextY + layerHeight2;
        path.add(new Point2D(finalX, finalY));

        return path;
    }

    private double convertLayerToRefractionIndex(HBox layer) {
        // Get the fill color from the layer's background
        Paint color = layer.getBackground().getFills().get(0).getFill();

        if (color.equals(Color.web("#FFFFFF"))) { // Air
            return 1.0003;
        } else if (color.equals(Color.web("#00CDFF"))) { // Water
            return 1.33;
        } else if (color.equals(Color.web("#E8F8FF"))) { // Ice
            return 1.31;
        } else if (color.equals(Color.web("#FFD8D1"))) { // Rock salt
            return 1.54;
        } else if (color.equals(Color.web("#E6F1FF"))) { // Diamond
            return 2.42;
        } else if (color.equals(Color.web("#F0F8FF"))) { // Glass
            return 1.52;
        } else {
            throw new IllegalArgumentException("Unknown Layer");
        }
    }

    private Timeline createAnimation(List<Point2D> path, Duration totalDuration) {
        Timeline timeline = new Timeline();

        if (path.size() < 2) {
            return timeline; // No movement needed
        }

        Circle object = refractionView.getObject();

        Point2D start = path.get(0);
        object.setTranslateX(start.getX());
        object.setTranslateY(start.getY());

        // Calculate time per segment (a layer's length)
        int segmentCount = path.size() - 1;
        double segmentDuration = totalDuration.toMillis() / segmentCount;

        // Add keyframes for each point in the path
        for (int i = 1; i < path.size(); i++) {
            Point2D target = path.get(i);
            double time = i * segmentDuration;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(time),
                    new KeyValue(object.translateXProperty(), target.getX(), javafx.animation.Interpolator.LINEAR),
                    new KeyValue(object.translateYProperty(), target.getY(), javafx.animation.Interpolator.LINEAR)
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setCycleCount(1);
        return timeline;
    }

    private boolean isInTheRange(Circle object) {
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
