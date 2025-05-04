package project.optics.jfkt.utils;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.controllers.GeneralSettingsController;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.models.GeneralSetting;

import project.optics.jfkt.views.GeneralSettingView;
import project.optics.jfkt.views.MainView;
import project.optics.jfkt.views.ThemeView;


import static project.optics.jfkt.MainApp.primaryStage;

public class Util {

    private VBox aboutUsContainer;
    private VBox helpContainer;
    private Scene aboutUsScene;
    private Scene helpScene;
    private ThemeController themeController = new ThemeController();
    private GeneralSettingsController generalSettingsController = new GeneralSettingsController();

    public void switchScene(Scene newScene) {
        ThemeController.applyTheme(newScene);
        primaryStage.setScene(newScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void applyFontToScene(Scene scene) {
        if (scene != null) {
            String currentFont = ThemeController.getCurrentFont();
            scene.getRoot().setStyle("-fx-font-family: '" + currentFont + "';");
        }
    }
    public Region createMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu(GeneralSetting.getString("menu.file"));
        MenuItem quit = new MenuItem(GeneralSetting.getString("menuItem.quit"));
        MenuItem aboutUs = new MenuItem(GeneralSetting.getString("menuItem.aboutUs"));
        MenuItem help = new MenuItem(GeneralSetting.getString("menuItem.help"));
        Menu settings = new Menu(GeneralSetting.getString("menu.settings"));

        MenuItem theme = new MenuItem(GeneralSetting.getString("menuItem.themeSetting"));
        MenuItem general = new MenuItem(GeneralSetting.getString("menuItem.generalSetting"));

        quit.setOnAction(e -> onQuitButtonPressed());
        aboutUs.setOnAction(e -> onAboutUsPressed());
        help.setOnAction(e -> onHelpPressed());

       theme.setOnAction(e->onThemeButtonPressed());
        general.setOnAction(event -> onGeneralSettingsButtonPressed());

        fileMenu.getItems().addAll(quit,aboutUs, help);

        settings.getItems().addAll(theme, general);

        menuBar.getMenus().addAll(fileMenu,settings );

        return menuBar;
    }
    public void onThemeButtonPressed() {
        ThemeView themeView = new ThemeView(themeController);
        Scene scene = new Scene(themeView);
        switchScene(scene);
    }

    public void onGeneralSettingsButtonPressed() {
        GeneralSettingView generalSettingView = new GeneralSettingView(generalSettingsController);
        Scene scene = new Scene(generalSettingView);
       switchScene(scene);
    }

    public void onQuitButtonPressed() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(GeneralSetting.getString("text.confirmation"));
        confirmationAlert.setHeaderText(GeneralSetting.getString("text.exitApplication"));
        confirmationAlert.setContentText(GeneralSetting.getString("text.sureToExit"));

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                primaryStage.close();
            }
        });
    }
    public void onAboutUsPressed() {
        if (aboutUsContainer == null) {
            aboutUsContainer = new VBox(20);
            aboutUsContainer.setAlignment(Pos.CENTER);
            aboutUsContainer.setPadding(new Insets(20));

            // Create the UI elements
            Label aboutUsText = new Label(GeneralSetting.getString("menuItem.aboutUs"));
            aboutUsText.getStyleClass().add("about-us-title"); // Add style class

            Label fillerText = new Label(
                    GeneralSetting.getString("menuItem.aboutUs.content")
            );
            fillerText.getStyleClass().add("about-us-content"); // Add style class
            fillerText.setTextAlignment(TextAlignment.CENTER);

            Button backButton = new Button(GeneralSetting.getString("button.back"));
            backButton.getStyleClass().add("about-us-button"); // Add style class
            backButton.setOnAction(e -> {
                switchScene(new Scene(new MainView(MainApp.primaryStage)));
            });

            aboutUsContainer.getChildren().addAll(aboutUsText, fillerText, backButton);
            aboutUsScene = new Scene(aboutUsContainer, 400, 300);
            ThemeController.applyTheme(aboutUsScene);
        }

        // Apply current font to all elements
        String currentFont = ThemeController.getCurrentFont();
        aboutUsContainer.setStyle("-fx-font-family: '" + currentFont + "';");

        // Explicitly set font for each element (in case CSS overrides)
        for (var node : aboutUsContainer.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-font-family: '" + currentFont + "';");
            } else if (node instanceof Button) {
                ((Button) node).setStyle("-fx-font-family: '" + currentFont + "';");
            }
        }

       switchScene(aboutUsScene);
    }
    public void onHelpPressed() {
        if (helpContainer == null) {
            helpContainer = new VBox(20);
            helpContainer.setAlignment(Pos.CENTER);
            helpContainer.setPadding(new Insets(20));
            helpContainer.getStyleClass().add("help-container");

            Label helpHeading = new Label(GeneralSetting.getString("text.help.title"));
            helpHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            helpHeading.getStyleClass().add("help-heading");

            TextFlow helpTextFlow = new TextFlow();
            helpTextFlow.getStyleClass().add("help-text-flow");

            // Create all text elements with style classes
            Text[] textElements = {
                    createHelpText(GeneralSetting.getString("help.welcome"), false),
                    createHelpText(GeneralSetting.getString("help.programDescription"), false),
                    createHelpText(GeneralSetting.getString("help.refraction.heading"), true),
                    createHelpText(GeneralSetting.getString("help.refraction.formula"), false),
                    createHelpText(GeneralSetting.getString("help.refraction.points"), false),
                    createHelpText(GeneralSetting.getString("help.thinLens.heading"), true),
                    createHelpText(GeneralSetting.getString("help.thinLens.formula"), false),
                    createHelpText(GeneralSetting.getString("help.thinLens.points"), false),
                    createHelpText(GeneralSetting.getString("help.mirror.heading"), true),
                    createHelpText(GeneralSetting.getString("help.mirror.formula"), false),
                    createHelpText(GeneralSetting.getString("help.mirror.points"), false),
                    createHelpText(GeneralSetting.getString("help.magnification.heading"), true),
                    createHelpText(GeneralSetting.getString("help.magnification.formula"), false),
                    createHelpText(GeneralSetting.getString("help.magnification.points"), false)
            };

            helpTextFlow.getChildren().addAll(textElements);

            HBox textFlowContainer = new HBox(helpTextFlow);
            textFlowContainer.setAlignment(Pos.CENTER);

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> {
                switchScene(new Scene(new MainView(MainApp.primaryStage)));
            });

            helpContainer.getChildren().addAll(helpHeading, textFlowContainer, backButton);
            helpScene = new Scene(helpContainer, 600, 500);
            ThemeController.applyTheme(helpScene);
        }

        applyFontToScene(helpScene);
        switchScene(helpScene);
    }

    private Text createHelpText(String content, boolean isHeading) {
        Text text = new Text(content);
        text.getStyleClass().add("help-text");
        if (isHeading) {
            text.setUnderline(true);
        }
        return text;
    }

    public HBox createZoomAndBackButtons() {
        HBox container = new HBox(20);

        Button zoomIn = new Button(GeneralSetting.getString("button.zoomIn"));
        Button zoomOut = new Button(GeneralSetting.getString("button.zoomOut"));
        Button back = new Button(GeneralSetting.getString("button.back"));
        back.setOnAction(event -> switchScene(new Scene(new MainView(primaryStage))));

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
