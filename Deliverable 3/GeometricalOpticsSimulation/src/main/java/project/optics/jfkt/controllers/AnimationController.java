package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.MainView;

import static project.optics.jfkt.MainApp.primaryStage;
import static project.optics.jfkt.controllers.ThemeController.applyTheme;

public class AnimationController {

    public void onBackButtonPressed() {
        MainView mainView = new MainView(MainApp.primaryStage);
        Scene scene = new Scene(mainView);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}

