package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.MainView;

public class GeneralSettingsController {
    private final Util util = new Util();
    public void onBackButtonPressed() {
        util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
    }
}




