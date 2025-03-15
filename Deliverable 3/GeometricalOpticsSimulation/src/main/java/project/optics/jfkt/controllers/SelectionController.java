package project.optics.jfkt.controllers;

import com.sun.tools.javac.Main;
import javafx.scene.Scene;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.EducationModeView;
import project.optics.jfkt.views.MainView;

public class SelectionController {
    private final Util util = new Util();

    public void onBackButtonPressed() {
        util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
    }

    public void onEasyButtonPressed() {
        util.switchScene(new Scene(new EducationModeView(Difficulty.EASY)));
    }

    public void onMediumButtonPressed() {
        util.switchScene(new Scene(new EducationModeView(Difficulty.MEDIUM)));
    }

    public void onHardButtonPressed() {
        util.switchScene(new Scene(new EducationModeView(Difficulty.HARD)));
    }
}
