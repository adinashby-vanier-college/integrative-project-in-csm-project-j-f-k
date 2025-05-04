package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.optics.jfkt.controllers.LoginController;

public class LoginView extends VBox {
    private Hyperlink createLink;
    private final LoginController controller;
    private final Stage primaryStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;

    public LoginView(Stage primaryStage, LoginController controller) {
        this.primaryStage = primaryStage;
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        // Center the VBox content both horizontally and vertically
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(30));

        // Title
        Label titleLabel = new Label("Geometric Optics Simulation Login");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Grid for form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setMaxWidth(300);

        // Username field
        grid.add(new Label("Username:"), 0, 0);
        usernameField = new TextField();
        grid.add(usernameField, 1, 0);

        // Password field
        grid.add(new Label("Password:"), 0, 1);
        passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        // Buttons: Refresh & Login
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> handleLogin());
        buttonBox.getChildren().addAll( loginButton);
        grid.add(buttonBox, 1, 2);

        // Create Account link
        createLink = new Hyperlink("Create Account");
        createLink.setOnAction(event -> controller.onLinkClicked(primaryStage));
        HBox linkBox = new HBox();
        linkBox.setAlignment(Pos.CENTER);
        linkBox.setPadding(new Insets(10, 0, 0, 0));
        linkBox.getChildren().add(createLink);
        grid.add(linkBox, 1, 3);

        // Status label
        statusLabel = new Label();
        grid.add(statusLabel, 0, 4, 2, 1);

        getChildren().addAll(titleLabel, grid);
    }

    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (controller.handleLogin(username, password)) {

                MainView mainView = new MainView(primaryStage);
                primaryStage.getScene().setRoot(mainView);
            } else {
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
        } finally {
            passwordField.clear();
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
    public void setCredentials(String username, String password) {
        this.usernameField.setText(username);
        this.passwordField.setText(password);
    }
}