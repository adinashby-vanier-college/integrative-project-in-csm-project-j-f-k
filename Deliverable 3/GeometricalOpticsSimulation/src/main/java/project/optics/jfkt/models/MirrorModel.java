package project.optics.jfkt.models;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MirrorModel {
    private Group mirrorGroup;

    public MirrorModel(double focalLength, double centerX, double centerY, double scale) {
        double radius = Math.abs(2 * focalLength) * scale;

        mirrorGroup = new Group();

        // Create mirror arcs
        Arc mirrorArc = new Arc();
        mirrorArc.setCenterX(centerX + radius);
        mirrorArc.setCenterY(centerY);
        mirrorArc.setRadiusX(radius);
        mirrorArc.setRadiusY(radius);
        mirrorArc.setStartAngle(90);
        mirrorArc.setLength(180);
        mirrorArc.setType(ArcType.OPEN);
        mirrorArc.setStroke(Color.BLUE);
        mirrorArc.setStrokeWidth(2);
        mirrorArc.setFill(Color.TRANSPARENT);

        Arc innerArc = new Arc();
        innerArc.setCenterX(centerX + radius);
        innerArc.setCenterY(centerY);
        innerArc.setRadiusX(radius * 0.95);
        innerArc.setRadiusY(radius * 0.95);
        innerArc.setStartAngle(90);
        innerArc.setLength(180);
        innerArc.setType(ArcType.OPEN);
        innerArc.setStroke(Color.BLUE);
        innerArc.setStrokeWidth(1);
        innerArc.setFill(Color.TRANSPARENT);
        innerArc.getStrokeDashArray().addAll(5.0, 5.0);

        // Create focal point
        Circle focalPoint = new Circle(5);
        focalPoint.setFill(Color.RED);
        focalPoint.setStroke(Color.BLACK);

        double mirrorSurfaceX = centerX + radius;
        double focalX = mirrorSurfaceX + Math.abs(focalLength * scale);
        focalPoint.setCenterX(focalX);
        focalPoint.setCenterY(centerY);

        Text focalLabel = new Text("F");
        focalLabel.setFont(Font.font(12)); // Set font size
        focalLabel.setFill(Color.BLACK);


        focalLabel.setX(focalX - 5); // Center horizontally (approx)
        focalLabel.setY(centerY + 20); // 20 pixels below focal point


        mirrorGroup.getChildren().addAll(mirrorArc, innerArc, focalPoint, focalLabel);

        if (focalLength < 0) {
            mirrorGroup.setScaleX(-1);
            mirrorGroup.getChildren().get(3).setScaleX(-1);
        }
    }

    public Group getMirrorGroup() {
        return mirrorGroup;
    }
}