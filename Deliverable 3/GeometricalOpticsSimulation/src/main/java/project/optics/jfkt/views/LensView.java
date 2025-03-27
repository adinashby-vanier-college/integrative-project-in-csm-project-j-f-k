package project.optics.jfkt.views;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class LensView extends BaseView {
    private static final double DEFAULT_SCALE = 50.0; // Pixels per unit
    private double scale = DEFAULT_SCALE;
    private double centerY;
    private double centerX;

    public LensView() {
        super("Lenses");
        Pane animPane = getAnimpane();
        animPane.getChildren().clear();
        centerY = animPane.getPrefHeight() / 2;
        centerX = animPane.getPrefWidth() / 2;

        // Set default values
        showDefaultLensSystem();
    }

    private void showDefaultLensSystem() {
        // Default values
        int defaultNumRays = 3;
        double defaultObjectDistance = 8.0;
        double defaultObjectHeight = 2.0;
        double defaultMagnification = -0.5;
        double defaultFocalLength = 4.0;
        List<Double> defaultExtraLenses = new ArrayList<>();

        updateView(defaultNumRays, defaultObjectDistance, defaultObjectHeight,
                defaultMagnification, defaultFocalLength, defaultExtraLenses);
    }

    @Override
    protected Pane createCenter() {
        Pane baseCenter = (Pane) super.createCenter();

        // Find the VBox containing parameters
        VBox paramVBox = null;
        for (Node node : baseCenter.getChildren()) {
            if (node instanceof VBox) {
                paramVBox = (VBox) node;
                break;
            }
        }

        // If found, clear existing parameters and add new ones
        if (paramVBox != null) {
            paramVBox.getChildren().clear(); // Clear existing parameters

            // Add header back
            Text paramheadertext = new Text("Parameters:");
            paramheadertext.setFont(new Font(40));
            paramheadertext.setTextAlignment(TextAlignment.CENTER);
            paramheadertext.setUnderline(true);
            paramVBox.getChildren().add(paramheadertext);

            // Add parameters with default values
            paramVBox.getChildren().addAll(
                    createParamHbox("Object Distance", "8.0"),
                    createParamHbox("Object Height", "2.0"),
                    createParamHbox("Focal Length", "4.0"),
                    createParamHbox("Magnification", "-0.5"),
                    createParamHbox("Number of Rays", "3")
            );
        }

        return baseCenter;
    }

    // Overloaded version of createParamHbox with default value
    private HBox createParamHbox(String text, String defaultValue) {
        HBox hbox = createParamHbox(text);
        TextField textField = (TextField) hbox.getChildren().get(1);
        textField.setText(defaultValue);
        return hbox;
    }

    public void updateView(int numRays, double objectDistance, double objectHeight,
                           double magnification, double focalLength, List<Double> extraLenses) {
        Pane animPane = getAnimpane();
        animPane.getChildren().clear(); // Clear previous elements

        // Draw optical axis (horizontal center line)
        Line opticalAxis = new Line(0, centerY, animPane.getPrefWidth(), centerY);
        opticalAxis.setStroke(Color.BLACK);
        opticalAxis.setStrokeWidth(1);
        animPane.getChildren().add(opticalAxis);

        // Draw main lens (vertical line at center)
        Line mainLens = new Line(centerX, centerY - 50, centerX, centerY + 50);
        mainLens.setStroke(Color.BLUE);
        mainLens.setStrokeWidth(3);
        animPane.getChildren().add(mainLens);

        // Draw focal points
        double focalLengthPx = focalLength * scale;
        drawFocalPoints(centerX, centerY, focalLengthPx, animPane);

        // Draw object (arrow)
        double objX = centerX - (objectDistance * scale);
        double objHeightPx = objectHeight * scale;
        drawObject(objX, centerY, objHeightPx, animPane);

        // Draw principal rays
        drawPrincipalRays(objX, centerY - objHeightPx, centerX, centerY,
                focalLengthPx, numRays, animPane);

        // Draw extra lenses if any
        for (double lensPos : extraLenses) {
            double extraLensX = centerX + (lensPos * scale);
            Line extraLens = new Line(extraLensX, centerY - 40, extraLensX, centerY + 40);
            extraLens.setStroke(Color.RED);
            extraLens.setStrokeWidth(2);
            extraLens.getStrokeDashArray().addAll(5d, 5d); // Make it dashed
            animPane.getChildren().add(extraLens);
        }
    }

    private void drawFocalPoints(double lensX, double centerY, double focalLengthPx, Pane pane) {
        // Left focal point (F)
        Circle leftFocal = new Circle(lensX - focalLengthPx, centerY, 5);
        leftFocal.setFill(Color.DARKBLUE);
        pane.getChildren().add(leftFocal);

        // Right focal point (F')
        Circle rightFocal = new Circle(lensX + focalLengthPx, centerY, 5);
        rightFocal.setFill(Color.DARKBLUE);
        pane.getChildren().add(rightFocal);

        // Labels
        Text leftLabel = new Text(lensX - focalLengthPx - 15, centerY - 10, "F");
        Text rightLabel = new Text(lensX + focalLengthPx + 10, centerY - 10, "F'");
        pane.getChildren().addAll(leftLabel, rightLabel);
    }

    private void drawObject(double x, double centerY, double heightPx, Pane pane) {
        // Object line
        Line objLine = new Line(x, centerY, x, centerY - heightPx);
        objLine.setStroke(Color.GREEN);
        objLine.setStrokeWidth(2);

        // Arrow head
        Polygon arrowHead = new Polygon(
                x, centerY - heightPx,
                x - 5, centerY - heightPx + 10,
                x + 5, centerY - heightPx + 10
        );
        arrowHead.setFill(Color.GREEN);

        // Label
        Text label = new Text(x - 20, centerY - heightPx/2, "Object");

        pane.getChildren().addAll(objLine, arrowHead, label);
    }

    private void drawPrincipalRays(double objX, double objY, double lensX, double lensY,
                                   double focalLengthPx, int numRays, Pane pane) {
        // Always draw the 3 principal rays regardless of numRays value

        // 1. Ray parallel to axis -> refracts through focal point
        Line parallelRay1 = new Line(objX, objY, lensX, objY);
        Line parallelRay2 = new Line(lensX, objY, lensX + focalLengthPx, lensY);
        parallelRay1.setStroke(Color.RED);
        parallelRay2.setStroke(Color.RED);

        // 2. Ray through center -> continues straight
        Line centralRay1 = new Line(objX, objY, lensX, lensY);
        Line centralRay2 = new Line(lensX, lensY, lensX + (lensX - objX), lensY + (lensY - objY));
        centralRay1.setStroke(Color.BLUE);
        centralRay2.setStroke(Color.BLUE);

        // 3. Ray through near focal point -> refracts parallel to axis
        Line focalRay1 = new Line(objX, objY, lensX, lensY + (objY - lensY) * 2);
        Line focalRay2 = new Line(lensX, lensY + (objY - lensY) * 2,
                lensX + focalLengthPx * 2, lensY + (objY - lensY) * 2);
        focalRay1.setStroke(Color.GREEN);
        focalRay2.setStroke(Color.GREEN);

        pane.getChildren().addAll(parallelRay1, parallelRay2, centralRay1, centralRay2, focalRay1, focalRay2);

        // Draw image if rays converge
        double imageX = lensX + (lensX - objX); // Simple approximation
        double imageHeight = Math.abs(objY - lensY) * (imageX - lensX)/(lensX - objX);

        if (focalLengthPx > 0) { // Converging lens
            Line imageLine = new Line(imageX, lensY, imageX, lensY - imageHeight);
            imageLine.setStroke(Color.PURPLE);
            imageLine.setStrokeWidth(2);

            Text imageLabel = new Text(imageX + 10, lensY - imageHeight/2, "Image");
            pane.getChildren().addAll(imageLine, imageLabel);
        }
    }
}