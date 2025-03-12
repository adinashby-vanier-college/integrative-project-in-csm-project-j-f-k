package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.optics.jfkt.views.*;

import java.io.IOException;

public class MainController {
    private Stage primaryStage;
    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
        VBox vbox1 = new VBox();
        Scene scene = new Scene(vbox1);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void onHelpPressed() {
        VBox vbox1 = new VBox();
        Scene scene = new Scene(vbox1);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void onThemeButtonPressed() {
        ThemeView themeView = new ThemeView();
        Scene scene = new Scene(themeView);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void onAnimationButtonPressed() {
        AnimationView animationView = new AnimationView();
        Scene scene = new Scene(animationView);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void onGeneralSettingsButtonPressed() {
        GeneralSettingView generalSettingView = new GeneralSettingView();
        Scene scene = new Scene(generalSettingView);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }



    public void onEducationButtonPressed() {
        SelectionView selectionView = new SelectionView();
        Scene scene = new Scene(selectionView);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void onThinLensesPressed() throws IOException {
        LensView lensView = new LensView();
        Scene scene = new Scene(lensView);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
