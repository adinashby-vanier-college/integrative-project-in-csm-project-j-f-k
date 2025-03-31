package project.optics.jfkt.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.enums.AnimationStatus;
import project.optics.jfkt.models.Refraction;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.LayerChoosingView;
import project.optics.jfkt.views.RefractionView;

import java.util.ArrayList;
import java.util.List;

public class RefractionController {
    private Refraction refraction;
    private RefractionView refractionView;
    private Timeline animation;

    public RefractionController(Refraction refraction, RefractionView refractionView) {
        this.refraction = refraction;
        this.refractionView = refractionView;
    }

    public void onNewLayerButtonPressed(RefractionView refractionView, ArrayList<HBox> layers, VBox frame, HBox plusSignLayer) {
        if (refraction.getLayerCount() < 4) {
            // create layer choosing stage
            Stage stage = new Stage();
            stage.initOwner(MainApp.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false); // Disable resizing
            stage.initStyle(StageStyle.UTILITY); // Hide minimize/maximize buttons
            stage.setTitle("Material selection");
            stage.setScene(new Scene(new LayerChoosingView(refractionView, stage, layers, frame, refraction.getLayerCount(), plusSignLayer, refraction)));
            stage.setFullScreen(false);
            stage.show();

            refractionView.setAnimationStatus(AnimationStatus.PREPARED);
            refractionView.getTrailPane().getChildren().clear();
            refractionView.getTrailPane().getChildren().add(refractionView.getObject());
            refractionView.getObject().setTranslateY(0);
        }
    }

    public void onIncidentLocationChanged(Number incidentLocation, Circle incidentPoint) {
        incidentPoint.setTranslateX(incidentLocation.doubleValue());
    }

    public void onPlayButtonPressed() {
        if (refractionView.getAnimationStatus() == AnimationStatus.PAUSED) {
            animation.play();
            refractionView.setAnimationStatus(AnimationStatus.IN_PROGRESS);
        }

        if (refractionView.getAnimationStatus() == AnimationStatus.PREPARED || refractionView.getAnimationStatus() == AnimationStatus.FINISHED) {
            boolean flag = update();
            List<Point2D> path;

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
                refractionView.setAnimationStatus(AnimationStatus.IN_PROGRESS);
            }
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
            double reflectedY = -5; // reflected back to y = 0

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
            double refractedX = (nextX - refraction.getInitialLocation()) * 2 + refraction.getInitialLocation(); // just double the midpoint's x because it's symmetric
            double refractedY = -5;
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
        double exitY = currentY + h3 + 5;
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
            double reflectedY = currentY - 5;     // back at the top.
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
        double finalY = nextY + layerHeight2 + 5;
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
            return timeline; // No movement
        }

        Circle object = refractionView.getObject();

        Point2D start = path.get(0);
        object.setTranslateX(start.getX());
        object.setTranslateY(start.getY());

        // Create a dotted trail using a Polyline.
        Polyline trail = new Polyline();
        for (Point2D point : path) {
            trail.getPoints().addAll(point.getX(), point.getY());
        }
        trail.setStroke(Color.BLACK);
        trail.setStrokeWidth(2);
        trail.getStrokeDashArray().addAll(5.0, 5.0); // create a dashed pattern (5px dash, 5px gap)

        // Add the trail to the animation pane
        Pane trailPane = refractionView.getTrailPane();

        trailPane.getChildren().clear();
        trailPane.getChildren().add(0, trail);
        trailPane.getChildren().add(refractionView.getObject());

        // Calculate time per segment
        int segmentCount = path.size() - 1;
        double segmentDuration = totalDuration.toMillis() / segmentCount;

        // Create keyframes to animate the object along the path.
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

        // Stop the animation when the object goes out of bounds.
        object.translateXProperty().addListener((observable, oldValue, newValue) -> {
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double totalHeight = 0;
            int layerCount = refraction.getLayerCount();
            if (layerCount == 2) {
                totalHeight = refraction.getLayer1().getHeight() + refraction.getLayer2().getHeight();
            } else if (layerCount == 3) {
                totalHeight = refraction.getLayer1().getHeight() +
                        refraction.getLayer2().getHeight() +
                        refraction.getLayer3().getHeight();
            }

            if (object.getTranslateX() > screenWidth + 3 ||
                    object.getTranslateY() < -3 ||
                    object.getTranslateY() > totalHeight + 3) {
                timeline.stop();
            }
        });

        timeline.setOnFinished(event -> refractionView.setAnimationStatus(AnimationStatus.FINISHED));
        return timeline;
    }

    public void onLayerCountChanged(int newValue) {
        Rectangle clip = refractionView.getRectangleClip();
        ArrayList<HBox> layers = refractionView.getLayers();
        StackPane animationPane = refractionView.getAnimationPane();

        if (animationPane == null) {
            return;
        }

        clip.widthProperty().unbind();
        clip.heightProperty().unbind();

        if (newValue == 2) {
            HBox layer1 = layers.get(0);
            HBox layer2 = layers.get(1);

            clip.heightProperty().bind(layer1.heightProperty().add(layer2.heightProperty()));
            clip.widthProperty().bind(animationPane.widthProperty());
        } else {
            clip.heightProperty().bind(animationPane.heightProperty());
            clip.widthProperty().bind(animationPane.widthProperty());
        }
    }

    public void onAnimationSpeedChanged(Toggle selectedSpeed) {
        RadioButton selectedButton = (RadioButton) selectedSpeed;
        String selectedTxt = selectedButton.getText();

        switch (selectedTxt) {
            case "Slow" -> animation.setRate(0.5);
            case "Normal" -> animation.setRate(1);
            case "Fast" -> animation.setRate(2);
        }
    }

    public void onPausePressed() {
        animation.pause();
        refractionView.setAnimationStatus(AnimationStatus.PAUSED);
    }

    public void onInitialLocationChanged(double location) {
        refractionView.getTrailPane().getChildren().clear();
        refractionView.getTrailPane().getChildren().add(refractionView.getObject());
        refractionView.setIncidentLocation(location);
        refractionView.setAnimationStatus(AnimationStatus.PREPARED);
        refractionView.getObject().setTranslateY(0);

    }

    public void onInitialAngleChanged(double angle) {
        refractionView.getTrailPane().getChildren().clear();
        refractionView.getTrailPane().getChildren().add(refractionView.getObject());
        refractionView.setIncidentAngle(angle);
        refractionView.setAnimationStatus(AnimationStatus.PREPARED);
        refractionView.getObject().setTranslateY(0);
    }

    public void onRefreshButtonPressed() {
        new Util().switchScene(new Scene(new RefractionView()));
    }

    public void onAnimationStatusChanged(AnimationStatus status, Slider locationSlider, Slider angleSlider, Button newLayerButton) {
        System.out.println(status);
        if (status == AnimationStatus.IN_PROGRESS || status == AnimationStatus.PAUSED) {
            locationSlider.setDisable(true);
            angleSlider.setDisable(true);
            newLayerButton.setDisable(true);
        } else {
            locationSlider.setDisable(false);
            angleSlider.setDisable(false);
            newLayerButton.setDisable(false);
        }
    }
}
