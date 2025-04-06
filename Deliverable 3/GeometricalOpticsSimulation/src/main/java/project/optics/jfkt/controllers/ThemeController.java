package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.MainView;
import project.optics.jfkt.MainApp;

public class ThemeController {
    private final Util util = new Util();
    private static String currentTheme = "light-mode"; // default theme

    public void onBackButtonPressed() {
        util.switchScene(new Scene(new MainView(MainApp.primaryStage)));
        applyTheme(new Scene(new MainView(MainApp.primaryStage)));
    }

    public void setDarkMode() {
        currentTheme = "dark-mode";
    }

    public void setLightMode() {
        currentTheme = "light-mode";
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void applyTheme(Scene scene) {
        if (scene != null) {
            // Clear existing style classes
            scene.getRoot().getStyleClass().removeAll("light-mode", "dark-mode");
            // Add current theme class
            scene.getRoot().getStyleClass().add(currentTheme);
            // Apply the theme stylesheet
            scene.getStylesheets().add(ThemeController.class.getResource("/css/theme.css").toExternalForm());
        }
    }
}