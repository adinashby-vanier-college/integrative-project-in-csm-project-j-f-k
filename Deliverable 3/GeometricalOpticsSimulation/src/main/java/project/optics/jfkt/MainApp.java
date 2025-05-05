package project.optics.jfkt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.optics.jfkt.controllers.LoginController;
import project.optics.jfkt.views.LoginView;
import project.optics.jfkt.views.MainView;

public class MainApp extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainApp.primaryStage = primaryStage;


        LoginController controller = new LoginController();
        LoginView loginView = new LoginView(primaryStage, controller);

        // Set up the stage
        primaryStage.setTitle("Geometrical Optics Simulation");
        primaryStage.setScene(new Scene(loginView, 800, 600));
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}