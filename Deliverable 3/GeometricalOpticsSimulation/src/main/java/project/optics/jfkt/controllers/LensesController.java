package project.optics.jfkt.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.swing.text.html.ImageView;

public class LensesController {
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button backtoMainButton;
    @FXML private Button playButton;
    @FXML private Button pauseButton;
    @FXML private Button restartButton;
    @FXML private Button fastButton;
    @FXML private Button normalButton;
    @FXML private Button slowButton;

    @FXML private TextField focalLengthField;
    @FXML private TextField objectDistanceField;
    @FXML private TextField objectHeightField;
    @FXML private TextField magnificationField;
    @FXML private TextField numberOfRaysField;

    @FXML private ImageView rulerImage;
    @FXML private ImageView angleImage;
    @FXML private ImageView convexLensImage;
    @FXML private ImageView concaveLensImage;

    @FXML private Text speedLabel;



    @FXML
    private void initialize() {
        System.out.println("LensesSceneController initialized!"); // Debug message
    }

    @FXML
    private void handleZoomIn() {
        System.out.println("Zooming in");
        // Add logic to zoom in on the lens scene
    }

    @FXML
    private void handleZoomOut() {
        System.out.println("Zooming out");
        // Add logic to zoom out of the lens scene
    }

    @FXML
    private void handleBackToMain() {
        System.out.println("Returning to main scene");
        // Implement scene switching logic to return to main scene
    }

    @FXML
    private void handlePlay() {
        handleGetParameters();
        System.out.println("Playing animation");
        // Start animation logic
    }

    @FXML
    private void handlePause() {
        System.out.println("Pausing animation");
        // Pause animation logic
    }

    @FXML
    private void handleRestart() {
        System.out.println("Restarting animation");
        // Restart animation logic
    }

    @FXML
    private void handleFast() {
        System.out.println("Setting speed to fast");
        // Set animation speed to fast
    }

    @FXML
    private void handleNormal() {
        System.out.println("Setting speed to normal");
        // Set animation speed to normal
    }

    @FXML
    private void handleSlow() {
        System.out.println("Setting speed to slow");
        // Set animation speed to slow
    }

    public void handleRuler(ActionEvent actionEvent) {
    }

    public void handleProtractor(ActionEvent actionEvent) {
    }

    // Method to get parameters from text fields
    @FXML
    private void handleGetParameters() {
        try {
            // Read and convert values from TextFields
            double focalLength = Double.parseDouble(focalLengthField.getText());
            double objectDistance = Double.parseDouble(objectDistanceField.getText());
            double objectHeight = Double.parseDouble(objectHeightField.getText());
            double magnification = Double.parseDouble(magnificationField.getText());
            int numberOfRays = Integer.parseInt(numberOfRaysField.getText());

            // Print for testing
            System.out.println("Focal Length: " + focalLength);
            System.out.println("Object Distance: " + objectDistance);
            System.out.println("Object Height: " + objectHeight);
            System.out.println("Magnification: " + magnification);
            System.out.println("Number of Rays: " + numberOfRays);

            // Chat gpt'd

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter numbers.");
        }
    }
}
