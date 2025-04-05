package project.optics.jfkt.models;


import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class ArrowModel extends Polygon {

    private Polygon arrowObject = new Polygon();

    public ArrowModel(double objectHeight, double scale, double objectDistance, double centerX, double centerY){

        double heightPx = Math.abs(objectHeight * scale);


        double arrowHeadHeight = heightPx * 0.3;
        double shaftWidth = heightPx * 0.1;
        double headWidth = heightPx * 0.3;


        Double[] points = {

                (double) 0, (double) 0,
                -headWidth/2, arrowHeadHeight,
                -shaftWidth/2, arrowHeadHeight,
                -shaftWidth/2, heightPx,
                shaftWidth/2, heightPx,
                shaftWidth/2, arrowHeadHeight,
                headWidth/2, arrowHeadHeight
        };


        arrowObject.getPoints().addAll(points);
        arrowObject.setLayoutX(centerX - (objectDistance * scale));
        arrowObject.setLayoutY(centerY- (objectHeight*scale));

        arrowObject.setFill(Color.BLUE);
        arrowObject.setStroke(Color.DARKBLUE);

        if (objectHeight < 0){
            arrowObject.setRotate(180);
            arrowObject.setLayoutY(centerY);
        }

    }

    public Polygon getArrowObject() {
        return arrowObject;
    }
}
