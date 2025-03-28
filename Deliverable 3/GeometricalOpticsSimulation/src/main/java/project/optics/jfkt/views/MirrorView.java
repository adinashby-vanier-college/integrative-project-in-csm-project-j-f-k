package project.optics.jfkt.views;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import project.optics.jfkt.models.MirrorModel;

public class MirrorView extends BaseView {
    private double centerX;
    private double centerY;
    private static final double DEFAULT_SCALE = 10; //pixels per cm
    private double scale = DEFAULT_SCALE;

    public MirrorView() {
        super("Mirrors");
        Pane animPane = getAnimpane();
        centerX = animPane.getPrefWidth()/2;
        centerY = animPane.getPrefHeight()/2;
        //place base mirror & Objects
        double defaultObjectDistance = 8.0;
        double defaultObjectHeight = 2.0;
        double defaultFocalLength = -8.0;
        MirrorModel baseMirror = new MirrorModel(defaultFocalLength,centerX, centerY, scale);
        animPane.getChildren().add(baseMirror.getMirrorGroup());
    }

}
