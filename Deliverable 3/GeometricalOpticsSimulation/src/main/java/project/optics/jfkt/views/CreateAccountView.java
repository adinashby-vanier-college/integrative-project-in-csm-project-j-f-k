package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import project.optics.jfkt.controllers.CreateAccountController;

public class CreateAccountView extends VBox {
    private Button backButton;
    private final CreateAccountController createAccountController = new CreateAccountController();

    public CreateAccountView() {
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

        grid.add(new Label("Username:"), 0, 0);
        grid.add(new TextField(), 1, 0);

        grid.add(new Label("Password:"), 0, 1);
        grid.add(new PasswordField(), 1, 1);

        Label passAgainLabel = new Label("Input Password Again:");
        passAgainLabel.setWrapText(true);
        passAgainLabel.setMaxWidth(140);
        grid.add(passAgainLabel, 0, 2);
        grid.add(new PasswordField(), 1, 2);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        backButton = new Button("Back");
        backButton.setOnAction(event -> createAccountController.onBackButtonPressed());
        Button submitButton = new Button("Submit");
        buttonBox.getChildren().addAll(backButton, submitButton);
        grid.add(buttonBox, 1, 3);

        getChildren().addAll(titleLabel, grid);
    }

    public Button getBackButton() {
        return backButton;
    }
}
