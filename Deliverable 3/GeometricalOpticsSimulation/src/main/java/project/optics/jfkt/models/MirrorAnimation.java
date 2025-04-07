package project.optics.jfkt.models;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;

public class MirrorAnimation extends Pane {
    private SequentialTransition totalAnimation;
    private ArrowModel imageArrow;

    public MirrorAnimation(Double velocity, ArrayList<CoordinateModel> firstCoordinateSet, ArrayList<CoordinateModel> secondCoordinateSet, ArrayList<CoordinateModel> thirdCoordinateSet , ArrowModel imageArrow){
        SequentialTransition lineOneAnim = timelineCreator(timeCalculator(velocity , firstCoordinateSet), firstCoordinateSet, Color.BLUE);
        SequentialTransition lineTwoAnim = timelineCreator(timeCalculator(velocity , secondCoordinateSet), secondCoordinateSet, Color.GREEN);
        SequentialTransition lineThreeAnim = timelineCreator(timeCalculator(velocity , thirdCoordinateSet), thirdCoordinateSet, Color.RED);

        Timeline imageAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imageArrow.getArrowObject().opacityProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(imageArrow.getArrowObject().opacityProperty(), 1)
                )
        );

        imageArrow.getArrowObject().setOpacity(0);
        this.getChildren().add(imageArrow.getArrowObject());

        ParallelTransition parallelAnimations = new ParallelTransition(
                lineOneAnim,
                lineTwoAnim,
                lineThreeAnim
        );

        totalAnimation = new SequentialTransition(
                parallelAnimations,
                imageAnim
        );




    }


    ArrayList<Double> timeCalculator(Double velocity, ArrayList<CoordinateModel> coordinateSet){
        double distance;
        ArrayList<Double> animTime = new ArrayList<>();
        for (int i = 0 ; i < 2; i++){
            distance = Math.sqrt(Math.pow(coordinateSet.get(i+1).getX() - coordinateSet.get(i).getX(), 2) + Math.pow(coordinateSet.get(i+1).getY() - coordinateSet.get(i).getY(), 2));
            animTime.add(distance/velocity);
        }
        return  animTime;
    }

    SequentialTransition timelineCreator(ArrayList<Double> animTime, ArrayList<CoordinateModel> coordinateSet, Color color){
        Double startX = coordinateSet.getFirst().getX();
        Double startY = coordinateSet.getFirst().getY();
        Line lineOne = new Line(startX, startY, startX, startY);
        lineOne.setStrokeWidth(4);
        lineOne.setStroke(color);
        lineOne.setOpacity(0);

        Line lineTwo = new Line(
                coordinateSet.get(1).getX(),
                coordinateSet.get(1).getY(),
                coordinateSet.get(1).getX(),
                coordinateSet.get(1).getY()
        );
        lineTwo.setOpacity(0);
        lineTwo.setStrokeWidth(4);
        lineTwo.setStroke(color);

        Timeline showLine1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(lineOne.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(lineOne.opacityProperty(), 1)
                )
        );

        Timeline animateLine1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(lineOne.endXProperty(), lineOne.getStartX()),
                        new KeyValue(lineOne.endYProperty(), lineOne.getStartY())
                ),
                new KeyFrame(Duration.seconds(animTime.getFirst()),
                        new KeyValue(lineOne.endXProperty(), coordinateSet.get(1).getX()),
                        new KeyValue(lineOne.endYProperty(), coordinateSet.get(1).getY())
                )
        );

        Timeline showLine2 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(lineTwo.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(lineTwo.opacityProperty(), 1)
                )
        );

        Timeline animateLine2 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(lineTwo.endXProperty(), lineTwo.getStartX()),
                        new KeyValue(lineTwo.endYProperty(), lineTwo.getStartY())
                ),
                new KeyFrame(Duration.seconds(animTime.getLast()),
                        new KeyValue(lineTwo.endXProperty(), coordinateSet.getLast().getX()),
                        new KeyValue(lineTwo.endYProperty(), coordinateSet.getLast().getY())
                )
        );

        SequentialTransition sequentialTransition = new SequentialTransition(
                showLine1,
                animateLine1,
                showLine2,
                animateLine2
        );

        this.getChildren().addAll(lineOne, lineTwo);

        return sequentialTransition;
    }

    public void animPause(){
        totalAnimation.pause();
    }

    public void animPlay(){
      totalAnimation.play();
    }

}
