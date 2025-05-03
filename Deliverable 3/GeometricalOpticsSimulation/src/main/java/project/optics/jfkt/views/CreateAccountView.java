// CreateAccountView.java
package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import project.optics.jfkt.controllers.CreateAccountController;
import project.optics.jfkt.controllers.LoginController;

public class CreateAccountView extends VBox {
    private final CreateAccountController controller;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public CreateAccountView(Stage primaryStage, LoginController loginController) {
        this.controller = new CreateAccountController(primaryStage, loginController);
        initializeUI();
    }

    private void initializeUI() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(30));

        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setMaxWidth(350);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);
        grid.getColumnConstraints().addAll(col1, col2);

        // Username field
        grid.add(new Label("Username:"), 0, 0);
        usernameField = new TextField();
        grid.add(usernameField, 1, 0);

        // Password field
        grid.add(new Label("Password:"), 0, 1);
        passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        // Confirm password field
        Label passAgainLabel = new Label("Confirm Password:");
        passAgainLabel.setWrapText(true);
        passAgainLabel.setMaxWidth(140);
        grid.add(passAgainLabel, 0, 2);
        confirmPasswordField = new PasswordField();
        grid.add(confirmPasswordField, 1, 2);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> controller.onBackButtonPressed());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> handleRegistration());

        buttonBox.getChildren().addAll(backButton, submitButton);
        grid.add(buttonBox, 1, 3);

        getChildren().addAll(titleLabel, grid);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch", "The passwords you entered do not match.");
            return;
        }

        try {
            if (controller.registerUser(username, password)) {
                showAlert("Success", "Account created successfully!");
                controller.onBackButtonPressed();
            }
        } catch (Exception e) {
            showAlert("Registration Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}