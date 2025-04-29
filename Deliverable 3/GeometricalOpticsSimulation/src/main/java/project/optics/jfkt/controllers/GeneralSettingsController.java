package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.MainView;

import java.util.Locale;

public class GeneralSettingsController {
    private final Util util = new Util();
    public void onBackButtonPressed() {
        util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
    }
    public void onVolumeChanged(double newValue) {
        GeneralSetting.setVolume(newValue);
    }

    public void onLanguageChanged(String language) {
        if (language.equalsIgnoreCase("French") || language.equalsIgnoreCase("Français")) {
            GeneralSetting.setLocale(Locale.FRENCH);
        } else {
            GeneralSetting.setLocale(Locale.ENGLISH);
        }
    }
}




