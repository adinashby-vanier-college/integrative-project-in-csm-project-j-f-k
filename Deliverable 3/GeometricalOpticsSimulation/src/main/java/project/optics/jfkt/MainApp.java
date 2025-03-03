package project.optics.jfkt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.optics.jfkt.views.LensView;

/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * JavaFX 20.0.2 is already linked to this project in the build.gradle file.
 * @link: https://openjfx.io/javadoc/22/
 * @see: Build Scripts/build.gradle
 * @author Frostybee.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        //System.out.println("Hello there!");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new LensView(),1800,1000);
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
