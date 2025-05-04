package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import project.optics.jfkt.controllers.AnimationController;
import project.optics.jfkt.models.GeneralSetting;

public class AnimationView extends BorderPane {

    public AnimationView(AnimationController controller) {
        // Create a VBox to hold the settings
        VBox settingsBox = new VBox(20); // 20 is the spacing between elements
        settingsBox.setAlignment(Pos.CENTER); // Center-align the content
        settingsBox.setPadding(new Insets(20)); // Add padding around the VBox

        // Add a heading for the settings page
        Label settingsHeading = new Label(GeneralSetting.getString("heading.animationSetting"));
        settingsHeading.setFont(Font.font(18)); // Set font size
        settingsHeading.setStyle("-fx-font-weight: bold;"); // Make the text bold

        // Zoom In Button
        Button zoomInButton = new Button(GeneralSetting.getString("button.zoomIn"));
        zoomInButton.setOnAction(e -> {
            // Handle zoom in logic
            System.out.println("Zooming In");
        });

        // Zoom Out Button
        Button zoomOutButton = new Button(GeneralSetting.getString("button.zoomOut"));
        zoomOutButton.setOnAction(e -> {
            // Handle zoom out logic
            System.out.println("Zooming Out");
        });



        // Rotate Screen Button
        Button rotateButton = new Button(GeneralSetting.getString("button.rotate"));
        rotateButton.setOnAction(e -> {
            // Handle screen rotation logic
            System.out.println("Rotating Screen");
        });

        Button backButton = new Button(GeneralSetting.getString("button.back"));
        backButton.setOnAction(e -> controller.onBackButtonPressed());


        settingsBox.getChildren().addAll(
                settingsHeading,
                zoomInButton, zoomOutButton,
                rotateButton,
                backButton
        );


        this.setCenter(settingsBox);
    }
}