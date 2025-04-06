package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;
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
        MainView mainView = new MainView(MainApp.primaryStage);
        Scene scene = new Scene(mainView);
        applyTheme(scene);
        util.switchScene(scene);
    }
    private static List<Consumer<String>> themeChangeListeners = new ArrayList<>();

    public static void addThemeChangeListener(Consumer<String> listener) {
        themeChangeListeners.add(listener);
    }

    public static void removeThemeChangeListener(Consumer<String> listener) {
        themeChangeListeners.remove(listener);
    }

    private static void notifyThemeChangeListeners() {
        for (Consumer<String> listener : themeChangeListeners) {
            listener.accept(currentTheme);
        }
    }
    // Modify your theme setting methods to notify listeners
    public void setDarkMode() {
        currentTheme = "dark-mode";
        notifyThemeChangeListeners();
    }

    public void setLightMode() {
        currentTheme = "light-mode";
        notifyThemeChangeListeners();
    }

    public static void setCurrentFont(String font) {
        currentFont = font;
        notifyFontChangeListeners();
        // Apply to all existing scenes if needed
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Scene scene = ((Stage) window).getScene();
                if (scene != null) {
                    applyTheme(scene);
                }
            }
        }
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

            // Apply the theme stylesheet (force reload)
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    ThemeController.class.getResource("/css/theme.css").toExternalForm()
            );

            // Force CSS application
            scene.getRoot().applyCss();
        }
    }
}