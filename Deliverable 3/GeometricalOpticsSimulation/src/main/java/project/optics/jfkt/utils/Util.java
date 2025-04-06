package project.optics.jfkt.utils;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.views.MainView;

public class Util {
    public void switchScene(Scene newScene) {
        ThemeController.applyTheme(newScene);
        MainApp.primaryStage.setScene(newScene);
        MainApp.primaryStage.setFullScreen(true);
        MainApp.primaryStage.show();
        MainApp.primaryStage.centerOnScreen();
    }

    public Region createMenu() {
        HBox container = new HBox();
        container.setBorder(Border.stroke(Paint.valueOf("Black")));

        MenuBar menuBar1 = new MenuBar();

        Menu settings = new Menu("Settings");
        MenuItem general = new MenuItem("General");
        MenuItem animation = new MenuItem("Animation");
        MenuItem theme = new MenuItem("Theme");
        settings.getItems().addAll(general, animation, theme);

        Menu help = new Menu("Help");
        Menu aboutUs = new Menu("About Us");

        menuBar1.getMenus().addAll(settings, help, aboutUs);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        MenuBar menuBar2 = new MenuBar();
        Menu quit = new Menu("Quit");
        menuBar2.getMenus().add(quit);


        container.getChildren().addAll(menuBar1, spacer, menuBar2);

        return container;
    }

    public HBox createZoomAndBackButtons() {
        HBox container = new HBox(20);

        Button zoomIn = new Button("Zoom In");
        Button zoomOut = new Button("Zoom Out");
        Button back = new Button("Back");
        back.setOnAction(event -> switchScene(new Scene(new MainView(MainApp.primaryStage))));

        container.getChildren().addAll(zoomIn, zoomOut, back);

        return container;
    }

    public Group createProtractor() {
        Group protractorGroup = new Group();
        double centerX = 75;
        double centerY = 75;
        double radius = 50;

        // Create semi-circle protractor base
        Arc protractorArc = new Arc(centerX, centerY, radius, radius, 0, 180);
        protractorArc.setType(ArcType.OPEN);
        protractorArc.setStroke(Color.BLACK);
        protractorArc.setFill(Color.TRANSPARENT);
        protractorArc.setStrokeWidth(2);

        // Create draggable lines with handles
        Group line1Group = createDraggableLine(centerX, centerY, radius);
        Group line2Group = createDraggableLine(centerX, centerY, radius);

        // Initial positions
        positionLine(line1Group, centerX + radius, centerY);
        positionLine(line2Group, centerX, centerY - radius);

        // Create angle display text
        Text angleText = new Text();
        angleText.setX(centerX + radius + 5);
        angleText.setY(centerY - 5);
        angleText.setFill(Color.BLACK);

        // Update angle when handles move
        Line line1 = (Line) line1Group.getChildren().get(0);
        Line line2 = (Line) line2Group.getChildren().get(0);
        line1.endXProperty().addListener((obs, oldVal, newVal) -> updateAngle(line1, line2, angleText));
        line1.endYProperty().addListener((obs, oldVal, newVal) -> updateAngle(line1, line2, angleText));
        line2.endXProperty().addListener((obs, oldVal, newVal) -> updateAngle(line1, line2, angleText));
        line2.endYProperty().addListener((obs, oldVal, newVal) -> updateAngle(line1, line2, angleText));

        // Add all parts to the protractor group
        protractorGroup.getChildren().addAll(protractorArc, line1Group, line2Group, angleText);

        // Add move handle (green circle)
        Circle moveHandle = new Circle(5);
        moveHandle.setFill(Color.LIMEGREEN);
        moveHandle.setCursor(Cursor.MOVE);
        moveHandle.setCenterX(centerX);
        moveHandle.setCenterY(centerY + radius / 2);
        protractorGroup.getChildren().add(moveHandle);

        // Move handle logic
        final double[] orgSceneXY = new double[2];
        moveHandle.setOnMousePressed(event -> {
            orgSceneXY[0] = event.getSceneX();
            orgSceneXY[1] = event.getSceneY();
            event.consume();
        });
        moveHandle.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - orgSceneXY[0];
            double deltaY = event.getSceneY() - orgSceneXY[1];
            protractorGroup.setTranslateX(protractorGroup.getTranslateX() + deltaX);
            protractorGroup.setTranslateY(protractorGroup.getTranslateY() + deltaY);
            orgSceneXY[0] = event.getSceneX();
            orgSceneXY[1] = event.getSceneY();
            event.consume();
        });

        // Add rotate handle (orange circle)
        Circle rotateHandle = new Circle(5);
        rotateHandle.setFill(Color.ORANGE);
        rotateHandle.setCursor(Cursor.HAND);
        rotateHandle.setCenterX(centerX + radius + 10);
        rotateHandle.setCenterY(centerY);
        protractorGroup.getChildren().add(rotateHandle);

        final double[] initialAngle = new double[1];
        final double[] initialRotation = new double[1];
        final Point2D[] centerPoint = new Point2D[1];

        rotateHandle.setOnMousePressed(event -> {
            centerPoint[0] = protractorGroup.localToScene(centerX, centerY);
            Point2D mousePoint = new Point2D(event.getSceneX(), event.getSceneY());
            initialAngle[0] = Math.toDegrees(Math.atan2(
                    mousePoint.getY() - centerPoint[0].getY(),
                    mousePoint.getX() - centerPoint[0].getX()
            ));
            initialRotation[0] = protractorGroup.getRotate();
            event.consume();
        });

        rotateHandle.setOnMouseDragged(event -> {
            Point2D currentCenter = protractorGroup.localToScene(centerX, centerY);
            Point2D mousePoint = new Point2D(event.getSceneX(), event.getSceneY());

            double currentAngle = Math.toDegrees(Math.atan2(
                    mousePoint.getY() - currentCenter.getY(),
                    mousePoint.getX() - currentCenter.getX()
            ));

            double rotationDelta = currentAngle - initialAngle[0];
            protractorGroup.setRotate(initialRotation[0] + rotationDelta);
            event.consume();
        });

        return protractorGroup;
    }

    private Group createDraggableLine(double centerX, double centerY, double radius) {
        Line line = new Line(centerX, centerY, centerX, centerY);
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(1.5);

        Circle handle = new Circle(3);
        handle.setFill(Color.BLUE);
        handle.setCursor(Cursor.HAND);
        // Position handle initially at center
        handle.setCenterX(centerX);
        handle.setCenterY(centerY);

        Group lineGroup = new Group(line, handle);

        // Drag handler for the handle with updated angle computation.
        handle.setOnMouseDragged((MouseEvent event) -> {
            double dx = event.getX() - centerX;
            double dy = event.getY() - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance == 0) {
                event.consume();
                return;
            }
            double angle;
            // If the mouse is above (or at) the center, compute normally.
            if (event.getY() <= centerY) {
                angle = Math.toDegrees(Math.acos(dx / distance));
            } else {
                // If below center, force the extreme based on x position.
                angle = dx >= 0 ? 0 : 180;
            }
            // Ensure angle remains within 0° to 180°
            if (angle < 0) {
                angle = 0;
            } else if (angle > 180) {
                angle = 180;
            }

            double newX = centerX + Math.cos(Math.toRadians(angle)) * radius;
            double newY = centerY - Math.sin(Math.toRadians(angle)) * radius;

            line.setEndX(newX);
            line.setEndY(newY);
            handle.setCenterX(newX);
            handle.setCenterY(newY);
            event.consume();
        });

        return lineGroup;
    }

    private void positionLine(Group lineGroup, double endX, double endY) {
        Line line = (Line) lineGroup.getChildren().get(0);
        Circle handle = (Circle) lineGroup.getChildren().get(1);

        line.setEndX(endX);
        line.setEndY(endY);
        handle.setCenterX(endX);
        handle.setCenterY(endY);
    }

    private void updateAngle(Line line1, Line line2, Text angleText) {
        Point2D center = new Point2D(line1.getStartX(), line1.getStartY());
        Point2D vec1 = new Point2D(line1.getEndX() - center.getX(), line1.getEndY() - center.getY());
        Point2D vec2 = new Point2D(line2.getEndX() - center.getX(), line2.getEndY() - center.getY());

        double mag1 = vec1.magnitude();
        double mag2 = vec2.magnitude();
        if (mag1 == 0 || mag2 == 0) return;

        double angle1 = Math.toDegrees(Math.acos(vec1.getX() / mag1));
        double angle2 = Math.toDegrees(Math.acos(vec2.getX() / mag2));

        if (vec1.getY() > 0) {
            angle1 = 360 - angle1;
        }
        if (vec2.getY() > 0) {
            angle2 = 360 - angle2;
        }

        double angleDiff = Math.abs(angle2 - angle1);
        if (angleDiff > 180) {
            angleDiff = 360 - angleDiff;
        }
        angleText.setText(String.format("%.2f°", angleDiff));
    }



}
