package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import project.optics.jfkt.controllers.GeneralSettingsController;
import project.optics.jfkt.models.GeneralSetting;

public class GeneralSettingView extends BorderPane {
    private final GeneralSettingsController generalSettingsController = new GeneralSettingsController();

    public GeneralSettingView(GeneralSettingsController controller) {
        // Create a VBox to hold the settings
        VBox settingsBox = new VBox(20); // 20 is the spacing between elements
        settingsBox.setAlignment(Pos.CENTER); // Center-align the content
        settingsBox.setPadding(new Insets(20)); // Add padding around the VBox

        // Add a heading for the settings page
        Label settingsHeading = new Label("General Settings");
        settingsHeading.setFont(Font.font(18)); // Set font size
        settingsHeading.setStyle("-fx-font-weight: bold;"); // Make the text bold

        // Volume Slider
        Label volumeLabel = new Label("Volume:");
        Slider volumeSlider = new Slider(0, 100, GeneralSetting.getVolume()); // Min: 0, Max: 100, Default: 50
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(50);
        volumeSlider.setMinorTickCount(5);
        volumeSlider.setBlockIncrement(10);
        volumeSlider.setPrefWidth(200); // Set the preferred width of the slider (shorter)

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> generalSettingsController.onVolumeChanged(newValue.doubleValue()));
        volumeSlider.valueProperty().bindBidirectional(GeneralSetting.volumeProperty());

        // Wrap the slider and label in an HBox
        HBox volumeBox = new HBox(10, volumeLabel, volumeSlider);
        volumeBox.setAlignment(Pos.CENTER);

        // Language Selection
        Label languageLabel = new Label("Language:");
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("English", "French"); // Add English and French options
        languageComboBox.setValue("English"); // Set default language
        languageComboBox.setVisibleRowCount(2);
        languageComboBox.setOnAction(event -> generalSettingsController.onLanguageChanged(languageComboBox.getValue()));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> controller.onBackButtonPressed());

        // Add components to the VBox
        settingsBox.getChildren().addAll(
                settingsHeading,
                volumeBox, // Add the HBox containing the slider and label
                languageLabel, languageComboBox,
                backButton
        );

        // Set the VBox as the center of the BorderPane
        this.setCenter(settingsBox);
    }
}