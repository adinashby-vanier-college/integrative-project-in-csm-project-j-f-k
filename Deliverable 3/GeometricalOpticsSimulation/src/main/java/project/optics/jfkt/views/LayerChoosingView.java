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
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.models.Refraction;

import java.util.ArrayList;

public class LayerChoosingView extends VBox {
    private Label airLbl;
    private Label waterLbl;
    private Label iceLbl;
    private Label rockSaltLbl;
    private Label diamondLbl;
    private Label glassLbl;

    public LayerChoosingView(RefractionView refractionView, Stage stage, ArrayList<HBox> layers, VBox frame,
                             int layerCount, HBox plusSignLayer, Refraction refraction) {
        LayerChoosingController layerChoosingController = new LayerChoosingController(refractionView, stage,
                layers, frame, layerCount, plusSignLayer, refraction);

        // Initialize with current font
        String currentFont = ThemeController.getCurrentFont();

        HBox air = new HBox();
        air.setBackground(Background.fill(Color.web("#FFFFFF")));
        air.setOnMouseClicked(event -> layerChoosingController.onAirPressed());
        VBox.setVgrow(air, Priority.ALWAYS);
        airLbl = new Label(GeneralSetting.getString("layer.air"));
        airLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        air.getChildren().add(airLbl);
        airLbl.setAlignment(Pos.CENTER);

        HBox water = new HBox();
        water.setBackground(Background.fill(Color.web("#00CDFF")));
        water.setOnMouseClicked(event -> layerChoosingController.onWaterPressed());
        VBox.setVgrow(water, Priority.ALWAYS);
        waterLbl = new Label(GeneralSetting.getString("layer.water"));
        waterLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        water.getChildren().add(waterLbl);
        waterLbl.setAlignment(Pos.CENTER);

        HBox ice = new HBox();
        ice.setBackground(Background.fill(Color.web("#E8F8FF")));
        ice.setOnMouseClicked(event -> layerChoosingController.onIcePressed());
        VBox.setVgrow(ice, Priority.ALWAYS);
        iceLbl = new Label(GeneralSetting.getString("layer.ice"));
        iceLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        ice.getChildren().add(iceLbl);
        iceLbl.setAlignment(Pos.CENTER);

        HBox rockSalt = new HBox();
        rockSalt.setBackground(Background.fill(Color.web("#FFD8D1")));
        rockSalt.setOnMouseClicked(event -> layerChoosingController.onRockSaltPressed());
        VBox.setVgrow(rockSalt, Priority.ALWAYS);
        rockSaltLbl = new Label(GeneralSetting.getString("layer.rockSalt"));
        rockSaltLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        rockSalt.getChildren().add(rockSaltLbl);
        rockSaltLbl.setAlignment(Pos.CENTER);

        HBox diamond = new HBox();
        diamond.setBackground(Background.fill(Color.web("#E6F1FF")));
        diamond.setOnMouseClicked(event -> layerChoosingController.onDiamondPressed());
        VBox.setVgrow(diamond, Priority.ALWAYS);
        diamondLbl = new Label(GeneralSetting.getString("layer.diamond"));
        diamondLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        diamond.getChildren().add(diamondLbl);
        diamondLbl.setAlignment(Pos.CENTER);

        HBox glass = new HBox();
        glass.setBackground(Background.fill(Color.web("#F0F8FF")));
        glass.setOnMouseClicked(event -> layerChoosingController.onGlassPressed());
        VBox.setVgrow(glass, Priority.ALWAYS);
        glassLbl = new Label(GeneralSetting.getString("layer.glass"));
        glassLbl.setStyle("-fx-font-family: '" + currentFont + "';");
        glass.getChildren().add(glassLbl);
        glassLbl.setAlignment(Pos.CENTER);

        this.setPrefSize(700, 400);
        this.getChildren().addAll(air, water, ice, rockSalt, diamond, glass);

        // Add font change listener
        ThemeController.addFontChangeListener(this::updateFonts);
    }

    private void updateFonts(String font) {
        String fontStyle = "-fx-font-family: '" + font + "';";

        if (airLbl != null) airLbl.setStyle(fontStyle);
        if (waterLbl != null) waterLbl.setStyle(fontStyle);
        if (iceLbl != null) iceLbl.setStyle(fontStyle);
        if (rockSaltLbl != null) rockSaltLbl.setStyle(fontStyle);
        if (diamondLbl != null) diamondLbl.setStyle(fontStyle);
        if (glassLbl != null) glassLbl.setStyle(fontStyle);
    }
}