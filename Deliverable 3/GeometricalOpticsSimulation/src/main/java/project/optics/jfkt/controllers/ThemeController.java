package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.MainView;
import project.optics.jfkt.MainApp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ThemeController {
    private final Util util = new Util();
    private static String currentTheme = "light-mode"; // default theme
    private static String currentFont = "Arial"; // default font
    private static List<Consumer<String>> fontChangeListeners = new ArrayList<>();

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

    public static void setCurrentFont(String font) {
        currentFont = font;
        notifyFontChangeListeners();
    }

    private static void notifyFontChangeListeners() {
        for (Consumer<String> listener : fontChangeListeners) {
            listener.accept(currentFont);
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static String getCurrentFont() {
        return currentFont;
    }

    public static void addFontChangeListener(Consumer<String> listener) {
        fontChangeListeners.add(listener);
    }

    public static void removeFontChangeListener(Consumer<String> listener) {
        fontChangeListeners.remove(listener);
    }

    public static void applyTheme(Scene scene) {
        if (scene != null) {
            // Clear existing style classes
            scene.getRoot().getStyleClass().removeAll("light-mode", "dark-mode");
            // Add current theme class
            scene.getRoot().getStyleClass().add(currentTheme);
            // Apply font
            scene.getRoot().setStyle("-fx-font-family: '" + currentFont + "';");
            // Apply the theme stylesheet
            scene.getStylesheets().add(ThemeController.class.getResource("/css/theme.css").toExternalForm());
        }
    }
}