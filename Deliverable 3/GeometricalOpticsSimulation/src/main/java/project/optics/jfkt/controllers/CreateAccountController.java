package project.optics.jfkt.controllers;

import javafx.stage.Stage;
import project.optics.jfkt.models.UserManager;
import project.optics.jfkt.views.LoginView;

public class CreateAccountController {
    private final Stage primaryStage;
    private final LoginController loginController;
    private final UserManager userManager;

    public CreateAccountController(Stage primaryStage, LoginController loginController) {
        this.primaryStage = primaryStage;
        this.loginController = loginController;
        this.userManager = loginController.getUserManager();
    }

    public boolean registerUser(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        try {
            String encryptedPassword = loginController.encrypt(password);
            userManager.createUser(username, encryptedPassword);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public void onBackButtonPressed() {
        LoginView loginView = new LoginView(primaryStage, loginController);
        primaryStage.getScene().setRoot(loginView);
    }
}