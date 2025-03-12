package project.optics.jfkt.views;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends BorderPane{
private Stage primaryStage;

    public MainView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.setCenter(createContent());
    }


    private Region createContent(){
        VBox container = new VBox();
        Region spacer = new Region();
        spacer.setPrefHeight(400);

        Button educationGameButton = new Button("Education Game");
        educationGameButton.setPrefSize(150, 20);
        Button refractionButton = new Button("Refraction");
        refractionButton.setPrefSize(150, 20);
        Button thinLensesButton = new Button("Thin Lenses");
        thinLensesButton.setPrefSize(150, 20);
        Button mirrorButton = new Button("Mirror");
        mirrorButton.setPrefSize(150, 20);


        HBox buttons = new HBox(150);
        buttons.getChildren().addAll(educationGameButton, refractionButton, thinLensesButton,mirrorButton);
        buttons.setAlignment(Pos.CENTER);

        container.getChildren().addAll( spacer,buttons);
        container.setAlignment(Pos.CENTER);


        return container;
    }

}
