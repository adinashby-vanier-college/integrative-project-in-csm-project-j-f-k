package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import project.optics.jfkt.controllers.ThemeController;


public class ThemeView extends BorderPane {

    public ThemeView(ThemeController controller) {
        // Create a VBox layout for the theme settings
        VBox themeSettingsBox = new VBox(20);
        themeSettingsBox.setAlignment(Pos.CENTER);
        themeSettingsBox.setPadding(new Insets(20));

        // Label to display sample text
        Label sampleText = new Label("Sample Text");
        sampleText.setFont(Font.font(16));

        // ComboBox for font selection
        ComboBox<String> fontComboBox = new ComboBox<>();
        fontComboBox.getItems().addAll("Arial", "Times New Roman", "Courier New", "Verdana", "Comic Sans MS");
        fontComboBox.setValue("Arial"); // Default font
        fontComboBox.setOnAction(e -> {
            String selectedFont = fontComboBox.getValue();
            sampleText.setFont(Font.font(selectedFont, 16));
        });

        // ColorPicker for background color selection
        ColorPicker colorPicker = new ColorPicker(Color.WHITE); // Default color
        colorPicker.setOnAction(e -> {
            Color selectedColor = colorPicker.getValue();
            this.setStyle("-fx-background-color: #" + selectedColor.toString().substring(2));
        });

        // Create a back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.onBackButtonPressed()); // Delegate to the controller

        // Add components to the VBox
        themeSettingsBox.getChildren().addAll(
                new Label("Choose Font:"),
                fontComboBox,
                new Label("Choose Background Color:"),
                colorPicker,
                sampleText,
                backButton
        );

        // Set the VBox as the center of the BorderPane
        this.setCenter(themeSettingsBox);
    }



}