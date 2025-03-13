package project.optics.jfkt.utils;

import com.sun.tools.javac.Main;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import project.optics.jfkt.MainApp;

import java.awt.*;

public class Util {


    public void switchScene(Scene newScene) {
        MainApp.primaryStage.setScene(newScene);
        MainApp.primaryStage.setFullScreen(true);
        MainApp.primaryStage.show();
        MainApp.primaryStage.centerOnScreen();
    }

    public Region createMenu() {
        HBox container = new HBox();
        container.setBorder(Border.stroke(Paint.valueOf("Black")));

        MenuBar menuBar1 = new MenuBar();

        Menu settings = new Menu("Settings");
        MenuItem general = new MenuItem("General");
        MenuItem animation = new MenuItem("Animation");
        MenuItem theme = new MenuItem("Theme");
        settings.getItems().addAll(general, animation, theme);

        Menu help = new Menu("Help");
        Menu aboutUs = new Menu("About Us");

        menuBar1.getMenus().addAll(settings, help, aboutUs);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        MenuBar menuBar2 = new MenuBar();
        Menu quit = new Menu("Quit");
        menuBar2.getMenus().add(quit);


        container.getChildren().addAll(menuBar1, spacer, menuBar2);

        return container;
    }

    public HBox createZoomAndBackButtons() {
        HBox container = new HBox(20);

        Button zoomIn = new Button("Zoom In");
        Button zoomOut = new Button("Zoom Out");
        Button back = new Button("Back");

        container.getChildren().addAll(zoomIn, zoomOut, back);

        return container;
    }



}
