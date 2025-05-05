package project.optics.jfkt.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.*;

import java.io.IOException;

public class MainController {
    private Stage primaryStage;
    private VBox aboutUsContainer;
    private VBox helpContainer;
    private Scene aboutUsScene;
    private Scene helpScene;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        ThemeController.addFontChangeListener(font -> {
            if (aboutUsScene != null) {
                applyFontToScene(aboutUsScene);
            }
            if (helpScene != null) {
                applyFontToScene(helpScene);
            }
        });
    }

    private final Util util = new Util();
    private ThemeController themeController = new ThemeController();
    private GeneralSettingsController generalSettingsController = new GeneralSettingsController();


    private void applyFontToScene(Scene scene) {
        if (scene != null) {
            String currentFont = ThemeController.getCurrentFont();
            scene.getRoot().setStyle("-fx-font-family: '" + currentFont + "';");
        }
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
                util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
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

        util.switchScene(aboutUsScene);
    }

    public void onHelpPressed() {
        if (helpContainer == null) {
            helpContainer = new VBox(20);
            helpContainer.setAlignment(Pos.CENTER);
            helpContainer.setPadding(new Insets(20));
            helpContainer.getStyleClass().add("help-container");

            Label helpHeading = new Label(GeneralSetting.getString("text.help"));
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

            Button backButton = new Button(GeneralSetting.getString("button.back"));
            backButton.setOnAction(e -> {
                util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
            });

            helpContainer.getChildren().addAll(helpHeading, textFlowContainer, backButton);
            helpScene = new Scene(helpContainer, 600, 500);
            ThemeController.applyTheme(helpScene);
        }

        applyFontToScene(helpScene);
        util.switchScene(helpScene);
    }

    private Text createHelpText(String content, boolean isHeading) {
        Text text = new Text(content);
        text.getStyleClass().add("help-text");
        if (isHeading) {
            text.setUnderline(true);
        }
        return text;
    }



    public void onEducationButtonPressed() {
        SelectionView selectionView = new SelectionView();
        Scene scene = new Scene(selectionView);
        util.switchScene(scene);
    }

    public void onThinLensesPressed() throws IOException {
        LensView lensView = new LensView();
        Scene scene = new Scene(lensView);
        util.switchScene(scene);
    }

    public void onRefractionButtonPressed() {
        util.switchScene(new Scene(new RefractionView()));
    }

    public void onMirrorPressed(){
        MirrorView mirrorView = new MirrorView();
        Scene scene = new Scene(mirrorView);
        util.switchScene(scene);
    }

}
