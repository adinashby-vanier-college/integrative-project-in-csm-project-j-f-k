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

    public MirrorModel(double focalLength, double centerX, double centerY, double scale, boolean isConcave) {
        double radius = Math.abs(2 * focalLength) * scale;

        mirrorGroup = new Group();
        Arc mirrorArc = new Arc();
        Arc innerArc = new Arc();
        Circle focalPoint = new Circle(5);
        double focalX;

        if (isConcave) {
            // Create mirror arcs
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

            focalPoint.setFill(Color.RED);
            focalPoint.setStroke(Color.BLACK);

            double mirrorSurfaceX = centerX + radius;
            focalX = mirrorSurfaceX + Math.abs(focalLength * scale);
            focalPoint.setCenterX(focalX);
            focalPoint.setCenterY(centerY);

        } else{

            mirrorArc.setCenterX(centerX - radius);
            mirrorArc.setCenterY(centerY);
            mirrorArc.setRadiusX(radius);
            mirrorArc.setRadiusY(radius);
            mirrorArc.setStartAngle(270);
            mirrorArc.setLength(180);
            mirrorArc.setType(ArcType.OPEN);
            mirrorArc.setStroke(Color.BLUE);
            mirrorArc.setStrokeWidth(2);
            mirrorArc.setFill(Color.TRANSPARENT);

            innerArc.setCenterX(centerX - radius);
            innerArc.setCenterY(centerY);
            innerArc.setRadiusX(radius * 0.95);
            innerArc.setRadiusY(radius * 0.95);
            innerArc.setStartAngle(270);
            innerArc.setLength(180);
            innerArc.setType(ArcType.OPEN);
            innerArc.setStroke(Color.BLUE);
            innerArc.setStrokeWidth(1);
            innerArc.setFill(Color.TRANSPARENT);
            innerArc.getStrokeDashArray().addAll(5.0, 5.0);


            focalPoint.setFill(Color.RED);
            focalPoint.setStroke(Color.BLACK);

            double mirrorSurfaceX = centerX - radius;
            focalX = mirrorSurfaceX - Math.abs(focalLength * scale);
            focalPoint.setCenterX(focalX);
            focalPoint.setCenterY(centerY);

        }

        Text focalLabel = new Text("F");
        focalLabel.setFont(Font.font(12));
        focalLabel.setFill(Color.BLACK);


        focalLabel.setX(focalX - 5);
        focalLabel.setY(centerY + 20); // 20 pixels below focal point


        mirrorGroup.getChildren().addAll(mirrorArc, innerArc, focalPoint, focalLabel);

    }

    public Group getMirrorGroup() {
        return mirrorGroup;
    }
}