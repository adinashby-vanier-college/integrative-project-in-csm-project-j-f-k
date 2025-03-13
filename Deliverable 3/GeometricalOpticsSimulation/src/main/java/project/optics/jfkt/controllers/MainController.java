package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.*;

import java.io.IOException;

public class MainController {
    private Stage primaryStage;
    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    private final Util util = new Util();


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
        util.switchScene(scene);
    }

    public void onHelpPressed() {
        VBox vbox1 = new VBox();
        Scene scene = new Scene(vbox1);
        util.switchScene(scene);
    }

    public void onThemeButtonPressed() {
        ThemeView themeView = new ThemeView();
        Scene scene = new Scene(themeView);
        util.switchScene(scene);
    }

    public void onAnimationButtonPressed() {
        AnimationView animationView = new AnimationView();
        Scene scene = new Scene(animationView);
        util.switchScene(scene);
    }

    public void onGeneralSettingsButtonPressed() {
        GeneralSettingView generalSettingView = new GeneralSettingView();
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
}
