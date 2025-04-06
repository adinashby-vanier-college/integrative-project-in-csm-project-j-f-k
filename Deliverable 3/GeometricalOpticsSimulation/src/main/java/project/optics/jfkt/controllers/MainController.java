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
    private AnimationController animationController = new AnimationController();

    private void applyFontToScene(Scene scene) {
        if (scene != null) {
            String currentFont = ThemeController.getCurrentFont();
            scene.getRoot().setStyle("-fx-font-family: '" + currentFont + "';");
        }
    }

    public void onQuitButtonPressed() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Exit Application");
        confirmationAlert.setContentText("Are you sure you want to quit ?");

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
            Label aboutUsText = new Label("About Us");
            aboutUsText.getStyleClass().add("about-us-title"); // Add style class

            Label fillerText = new Label(
                    "Welcome to our application!\n\n" +
                            "We are a team of passionate developers dedicated to creating amazing software.\n" +
                            "Our mission is to provide users with the best experience possible.\n\n" +
                            "Thank you for using our app!"
            );
            fillerText.getStyleClass().add("about-us-content"); // Add style class
            fillerText.setTextAlignment(TextAlignment.CENTER);

            Button backButton = new Button("Back");
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

            Label helpHeading = new Label("Help - Geometric Optics Formulas");
            helpHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            helpHeading.getStyleClass().add("help-heading");

            TextFlow helpTextFlow = new TextFlow();
            helpTextFlow.getStyleClass().add("help-text-flow");

            // Create all text elements with style classes
            Text[] textElements = {
                    createHelpText("Welcome to the Help section!\n\n", false),
                    createHelpText("This program is designed to help you learn and visualize geometric optics concepts.\n\n", false),
                    createHelpText("Refraction (Snell's Law):\n", true),
                    createHelpText("   n₁ sin(θ₁) = n₂ sin(θ₂)\n", false),
                    createHelpText("   - n₁, n₂: Refractive indices of the two media\n   - θ₁, θ₂: Angles of incidence and refraction\n\n", false),
                    createHelpText("Thin Lens Formula:\n", true),
                    createHelpText("   1/f = 1/v - 1/u\n", false),
                    createHelpText("   - f: Focal length of the lens\n   - v: Image distance\n   - u: Object distance\n\n", false),
                    createHelpText("Mirror Formula:\n", true),
                    createHelpText("   1/f = 1/v + 1/u\n", false),
                    createHelpText("   - f: Focal length of the mirror\n   - v: Image distance\n   - u: Object distance\n\n", false),
                    createHelpText("Magnification (m):\n", true),
                    createHelpText("   m = h'/h = -v/u\n", false),
                    createHelpText("   - h': Height of the image\n   - h: Height of the object\n\n", false)
            };

            helpTextFlow.getChildren().addAll(textElements);

            HBox textFlowContainer = new HBox(helpTextFlow);
            textFlowContainer.setAlignment(Pos.CENTER);

            Button backButton = new Button("Back");
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

    public void onThemeButtonPressed() {
        ThemeView themeView = new ThemeView(themeController);
        Scene scene = new Scene(themeView);
        util.switchScene(scene);
    }

    public void onAnimationButtonPressed() {
        AnimationView animationView = new AnimationView(animationController);
        Scene scene = new Scene(animationView);
        util.switchScene(scene);
    }

    public void onGeneralSettingsButtonPressed() {
        GeneralSettingView generalSettingView = new GeneralSettingView(generalSettingsController);
        Scene scene = new Scene(generalSettingView);
        util.switchScene(scene);
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
