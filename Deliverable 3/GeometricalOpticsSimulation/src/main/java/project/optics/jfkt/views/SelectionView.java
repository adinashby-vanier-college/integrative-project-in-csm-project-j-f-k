package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import project.optics.jfkt.controllers.SelectionController;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.utils.Util;

public class SelectionView extends BorderPane {
    private final Util util = new Util();
    private final SelectionController selectionController = new SelectionController();
    private Button hard, medium, easy, back;

    public SelectionView() {
        // Initialize with current theme and font
        this.getStyleClass().add(ThemeController.getCurrentTheme());
        applyCurrentFont();

        // Add font change listener
        ThemeController.addFontChangeListener(font -> {
            applyCurrentFont();
            updateButtonFonts();
        });

        this.setTop(util.createMenu());
        this.setCenter(createCenter());
        this.setBottom(createBottom());
    }

    private void applyCurrentFont() {
        this.setStyle("-fx-font-family: '" + ThemeController.getCurrentFont() + "';");
    }

    private void updateButtonFonts() {
        String fontFamily = ThemeController.getCurrentFont();
        String buttonStyle = "-fx-font-family: '" + fontFamily + "';";

        if (hard != null) hard.setStyle(buttonStyle);
        if (medium != null) medium.setStyle(buttonStyle);
        if (easy != null) easy.setStyle(buttonStyle);
        if (back != null) back.setStyle(buttonStyle);
    }

    private Region createCenter() {
        VBox vBox = new VBox(100);

        // create images
        HBox images = new HBox(75);
        images.setAlignment(Pos.CENTER);

        ImageView oneStar = new ImageView(new Image(this.getClass().getResource("/images/one_star.png").toExternalForm()));
        oneStar.setLayoutX(1);
        oneStar.setLayoutY(1);
        ImageView twoStar = new ImageView(new Image(this.getClass().getResource("/images/two_stars.png").toExternalForm()));
        twoStar.setLayoutX(1);
        twoStar.setLayoutY(1);
        ImageView threeStar = new ImageView(new Image(this.getClass().getResource("/images/three_stars.png").toExternalForm()));
        threeStar.setLayoutX(1);
        threeStar.setLayoutY(1);

        Pane container1 = new Pane();
        Pane container2 = new Pane();
        Pane container3 = new Pane();

        container1.setBorder(Border.stroke(Color.BLACK));
        container2.setBorder(Border.stroke(Color.BLACK));
        container3.setBorder(Border.stroke(Color.BLACK));

        container1.getChildren().add(oneStar);
        container2.getChildren().add(twoStar);
        container3.getChildren().add(threeStar);

        images.getChildren().addAll(container1, container2, container3);

        // create buttons to go to different levels
        HBox buttons = new HBox(150);
        buttons.setAlignment(Pos.CENTER);

        hard = new Button("Hard");
        medium = new Button("Medium");
        easy = new Button("Easy");

        // Apply current font to buttons
        updateButtonFonts();

        hard.setOnAction(event -> selectionController.onHardButtonPressed());
        medium.setOnAction(event -> selectionController.onMediumButtonPressed());
        easy.setOnAction(event -> selectionController.onEasyButtonPressed());

        buttons.getChildren().addAll(easy, medium, hard);

        vBox.getChildren().addAll(images, buttons);

        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private Region createBottom() {
        HBox container = new HBox();

        back = new Button("Back");
        // Apply current font to back button
        updateButtonFonts();
        back.setOnAction(event -> selectionController.onBackButtonPressed());

        HBox.setMargin(back, new Insets(0, 0, 100, 100));

        container.getChildren().addAll(back);

        return container;
    }
}