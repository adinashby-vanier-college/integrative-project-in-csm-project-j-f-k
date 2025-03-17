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
    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    private final Util util = new Util();
    private ThemeController themeController = new ThemeController();
    private GeneralSettingsController generalSettingsController = new GeneralSettingsController();
    private AnimationController animationController = new AnimationController();



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

        VBox vbox1 = new VBox(20);
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setPadding(new Insets(20));


        Label aboutUsText = new Label("About Us");
        aboutUsText.setFont(Font.font(18));
        aboutUsText.setStyle("-fx-font-weight: bold;");

        // Filler text , subject to change later
        Label fillerText = new Label(
                "Welcome to our application!\n\n" +
                        "We are a team of passionate developers dedicated to creating amazing software.\n" +
                        "Our mission is to provide users with the best experience possible.\n\n" +
                        "Thank you for using our app!"
        );
        fillerText.setFont(Font.font(14));
        fillerText.setTextAlignment(TextAlignment.CENTER);


        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {

            util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
        });


        vbox1.getChildren().addAll(aboutUsText, fillerText, backButton);


        Scene scene = new Scene(vbox1, 400, 300);
        util.switchScene(scene);
    }


    public void onHelpPressed() {

        VBox vbox1 = new VBox(20);
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setPadding(new Insets(20));

        // Add a heading for the help section
        Label helpHeading = new Label("Help - Geometric Optics Formulas");
        helpHeading.setFont(Font.font(18));
        helpHeading.setStyle("-fx-font-weight: bold;");


        TextFlow helpTextFlow = new TextFlow();


        Text welcomeText = new Text("Welcome to the Help section!\n\n");
        welcomeText.setFont(Font.font(14));

        Text descriptionText = new Text("This program is designed to help you learn and visualize geometric optics concepts.\n\n");
        descriptionText.setFont(Font.font(14));

        // Refraction section
        Text refractionHeading = new Text("Refraction (Snell's Law):\n");
        refractionHeading.setFont(Font.font(14));
        refractionHeading.setUnderline(true);
        Text refractionFormula = new Text("   n₁ sin(θ₁) = n₂ sin(θ₂)\n");
        refractionFormula.setFont(Font.font(14));

        Text refractionDescription = new Text("   - n₁, n₂: Refractive indices of the two media\n   - θ₁, θ₂: Angles of incidence and refraction\n\n");
        refractionDescription.setFont(Font.font(14));

        // Thin Lens section
        Text thinLensHeading = new Text("Thin Lens Formula:\n");
        thinLensHeading.setFont(Font.font(14));
        thinLensHeading.setUnderline(true);

        Text thinLensFormula = new Text("   1/f = 1/v - 1/u\n");
        thinLensFormula.setFont(Font.font(14));

        Text thinLensDescription = new Text("   - f: Focal length of the lens\n   - v: Image distance\n   - u: Object distance\n\n");
        thinLensDescription.setFont(Font.font(14));

        // Mirror section
        Text mirrorHeading = new Text("Mirror Formula:\n");
        mirrorHeading.setFont(Font.font(14));
        mirrorHeading.setUnderline(true);

        Text mirrorFormula = new Text("   1/f = 1/v + 1/u\n");
        mirrorFormula.setFont(Font.font(14));

        Text mirrorDescription = new Text("   - f: Focal length of the mirror\n   - v: Image distance\n   - u: Object distance\n\n");
        mirrorDescription.setFont(Font.font(14));

        // Magnification section
        Text magnificationHeading = new Text("Magnification (m):\n");
        magnificationHeading.setFont(Font.font(14));
        magnificationHeading.setUnderline(true);

        Text magnificationFormula = new Text("   m = h'/h = -v/u\n");
        magnificationFormula.setFont(Font.font(14));

        Text magnificationDescription = new Text("   - h': Height of the image\n   - h: Height of the object\n\n");
        magnificationDescription.setFont(Font.font(14));


        // Add all Text nodes to the TextFlow
        helpTextFlow.getChildren().addAll(
                welcomeText, descriptionText,
                refractionHeading, refractionFormula, refractionDescription,
                thinLensHeading, thinLensFormula, thinLensDescription,
                mirrorHeading, mirrorFormula, mirrorDescription,
                magnificationHeading, magnificationFormula, magnificationDescription

        );

        HBox textFlowContainer = new HBox(helpTextFlow);
        textFlowContainer.setAlignment(Pos.CENTER);
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
        });

        vbox1.getChildren().addAll(helpHeading, textFlowContainer, backButton);
        Scene scene = new Scene(vbox1, 600, 500);
        util.switchScene(scene);
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
