package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import project.optics.jfkt.controllers.LoginController;

public class LoginView extends VBox {
    private Hyperlink createLink;
    private final LoginController loginController = new LoginController();

    public LoginView() {
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

        // Username
        grid.add(new Label("Username:"), 0, 0);
        grid.add(new TextField(), 1, 0);

        // Password
        grid.add(new Label("Password:"), 0, 1);
        grid.add(new PasswordField(), 1, 1);

        // Buttons: Refresh & Login
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button refreshButton = new Button("Refresh");
        Button loginButton = new Button("Login");
        buttonBox.getChildren().addAll(refreshButton, loginButton);
        grid.add(buttonBox, 1, 2);

        // Create Account link
        createLink = new Hyperlink("Create Account");
        createLink.setOnAction(event -> loginController.onLinkClicked());
        HBox linkBox = new HBox();
        linkBox.setAlignment(Pos.CENTER);
        linkBox.setPadding(new Insets(10, 0, 0, 0));
        linkBox.getChildren().add(createLink);
        grid.add(linkBox, 1, 3);

        getChildren().addAll(titleLabel, grid);
    }

    public Hyperlink getCreateLink() {
        return createLink;
    }
}
