package project.optics.jfkt.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.List;

public class LensView extends BaseView {

    public LensView() {
        super("Lenses");
    }

    @Override
    protected Pane createCenter() {
        Pane baseCenter = (Pane) super.createCenter(); // Get BaseView’s center layout

        // Find the VBox containing parameters
        VBox paramVBox = null;
        for (Node node : baseCenter.getChildren()) {
            if (node instanceof VBox) {
                paramVBox = (VBox) node;
                break;
            }
        }

        // If found, add extra parameters
        if (paramVBox != null) {
            paramVBox.getChildren().addAll(
                    createParamHbox("Magnification"),
                    createParamHbox("Number of Rays")
            );
        }

        return baseCenter;


    }
    public void updateView(int numRays, double objectDistance, double objectHeight, double magnification, double focalLength, List<Double> extraLenses) {
        //getChildren().clear();

        // Main rays
        for (int i = 0; i < numRays; i++) {
            double angle = Math.toRadians(i * (180.0 / numRays)); // Angle for each ray
            double endX = objectDistance - focalLength * Math.cos(angle);
            double endY = objectHeight - focalLength * Math.sin(angle);

            // Check if the ray crosses any extra lens
            for (double lensX : extraLenses) {
                if (endX < lensX) {
                    // Ray hits the extra lens, modify its direction
                    endX = lensX;
                    endY = objectHeight / 2; // Example effect: Rays converge to the lens center
                }
            }
            Line ray = new Line(objectDistance, objectHeight, endX, endY);
            ray.setStroke(Color.BLUE);
            ray.setStrokeWidth(2);
            getChildren().add(ray);
        }
        // Draw extra lenses as vertical lines
        for (double lensX : extraLenses) {
            Line lens = new Line(lensX, 0, lensX, getHeight());
            lens.setStroke(Color.RED);
            lens.setStrokeWidth(3);
            getChildren().add(lens);
        }
    }


}
