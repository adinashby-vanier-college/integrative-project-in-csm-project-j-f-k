package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.optics.jfkt.controllers.ThemeController;

public class ThemeView extends BorderPane {

    public ThemeView(ThemeController controller) {
        // Apply current theme and font to this view
        this.getStyleClass().add(ThemeController.getCurrentTheme());
        this.setStyle("-fx-font-family: '" + ThemeController.getCurrentFont() + "';");

        VBox themeSettingsBox = new VBox(20);
        themeSettingsBox.setAlignment(Pos.CENTER);
        themeSettingsBox.setPadding(new Insets(20));

        // Sample text with current font
        Label sampleText = new Label("Sample Text");
        sampleText.setStyle("-fx-font-size: 16;");

        // ComboBox for font selection
        ComboBox<String> fontComboBox = new ComboBox<>();
        fontComboBox.getItems().addAll("Arial", "Times New Roman", "Courier New", "Verdana", "Comic Sans MS");
        fontComboBox.setValue(ThemeController.getCurrentFont());
        fontComboBox.setOnAction(e -> {
            String selectedFont = fontComboBox.getValue();
            ThemeController.setCurrentFont(selectedFont);

            this.setStyle("-fx-font-family: '" + selectedFont + "';");
        });

        // Dark mode and light mode buttons in a horizontal box
        HBox themeButtonsBox = new HBox(20);
        themeButtonsBox.setAlignment(Pos.CENTER);

        Button darkMode = new Button("Dark Mode");
        darkMode.setOnAction(e -> {
            controller.setDarkMode();
            ThemeController.applyTheme(this.getScene());
        });

        Button lightMode = new Button("Light Mode");
        lightMode.setOnAction(e -> {
            controller.setLightMode();
            ThemeController.applyTheme(this.getScene());
        });

        // Temporary image for dark mode
        ImageView darkModeImage = new ImageView(new Image("images/tempd.png"));
        darkModeImage.setFitWidth(300);
        darkModeImage.setFitHeight(300);

        // Temporary image for light mode
        ImageView lightModeImage = new ImageView(new Image("images/templ.png"));
        lightModeImage.setFitWidth(300);
        lightModeImage.setFitHeight(300);

        // Add spacing between the buttons and their corresponding images
        VBox darkModeBox = new VBox(10);
        darkModeBox.setAlignment(Pos.CENTER);
        darkModeBox.getChildren().addAll(darkModeImage, darkMode);

        VBox lightModeBox = new VBox(10);
        lightModeBox.setAlignment(Pos.CENTER);
        lightModeBox.getChildren().addAll(lightModeImage, lightMode);

        // Add the buttons and images to the HBox
        themeButtonsBox.getChildren().addAll(darkModeBox, lightModeBox);

        // Create a back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.onBackButtonPressed());

        // Add all components to the VBox
        themeSettingsBox.getChildren().addAll(
                new Label("Choose Font:"),
                fontComboBox,

                new Label("Choose Theme:"),
                themeButtonsBox,
                backButton
        );

        // Set the VBox as the center of the BorderPane
        this.setCenter(themeSettingsBox);
    }
}