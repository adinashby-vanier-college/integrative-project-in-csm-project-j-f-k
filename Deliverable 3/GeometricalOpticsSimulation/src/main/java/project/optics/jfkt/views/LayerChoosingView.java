package project.optics.jfkt.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import project.optics.jfkt.controllers.LayerChoosingController;

import java.util.ArrayList;

public class LayerChoosingView extends VBox {
    public LayerChoosingView(RefractionView refractionView, Stage stage, ArrayList<HBox> layers, VBox frame, int currentLayer, HBox plusSignLayer) {
        LayerChoosingController layerChoosingController = new LayerChoosingController(refractionView, stage, layers, frame, currentLayer, plusSignLayer);

        HBox air = new HBox();
        air.setBackground(Background.fill(Color.web("#FFFFFF")));
        air.setOnMouseClicked(event -> layerChoosingController.onAirPressed());
        VBox.setVgrow(air, Priority.ALWAYS);
        Label airLbl = new Label("Air, n = 1.0003");
        air.getChildren().add(airLbl);
        airLbl.setAlignment(Pos.CENTER);

        HBox water = new HBox();
        water.setBackground(Background.fill(Color.web("#00CDFF")));
        water.setOnMouseClicked(event -> layerChoosingController.onWaterPressed());
        VBox.setVgrow(water, Priority.ALWAYS);
        Label waterLbl = new Label("Water, n = 1.33");
        water.getChildren().add(waterLbl);
        waterLbl.setAlignment(Pos.CENTER);

        HBox ice = new HBox();
        ice.setBackground(Background.fill(Color.web("#E8F8FF")));
        ice.setOnMouseClicked(event -> layerChoosingController.onIcePressed());
        VBox.setVgrow(ice, Priority.ALWAYS);
        Label iceLbl = new Label("Ice, n = 1.31");
        ice.getChildren().add(iceLbl);
        iceLbl.setAlignment(Pos.CENTER);

        HBox rockSalt = new HBox();
        rockSalt.setBackground(Background.fill(Color.web("#FFD8D1")));
        rockSalt.setOnMouseClicked(event -> layerChoosingController.onRockSaltPressed());
        VBox.setVgrow(rockSalt, Priority.ALWAYS);
        Label rockSaltLbl = new Label("Rock salt, n = 1.54");
        rockSalt.getChildren().add(rockSaltLbl);
        rockSaltLbl.setAlignment(Pos.CENTER);

        HBox diamond = new HBox();
        diamond.setBackground(Background.fill(Color.web("#E6F1FF")));
        diamond.setOnMouseClicked(event -> layerChoosingController.onDiamondPressed());
        VBox.setVgrow(diamond, Priority.ALWAYS);
        Label diamondLbl = new Label("Diamond, n = 2.42");
        diamond.getChildren().add(diamondLbl);
        diamondLbl.setAlignment(Pos.CENTER);

        HBox glass = new HBox();
        glass.setBackground(Background.fill(Color.web("#F0F8FF")));
        glass.setOnMouseClicked(event -> layerChoosingController.onGlassPressed());
        VBox.setVgrow(glass, Priority.ALWAYS);
        Label glassLbl = new Label("Glass, n = 1.52");
        glass.getChildren().add(glassLbl);
        glassLbl.setAlignment(Pos.CENTER);

        this.setPrefSize(700, 400);
        this.getChildren().addAll(air, water, ice, rockSalt, diamond, glass);
    }
}
