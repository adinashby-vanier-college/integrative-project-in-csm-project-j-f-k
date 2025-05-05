package project.optics.jfkt.controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import project.optics.jfkt.enums.Material;
import project.optics.jfkt.models.Refraction;
import project.optics.jfkt.views.RefractionView;

import java.util.ArrayList;

public class LayerChoosingController {
    private final RefractionView refractionView;
    private final Stage stage;
    private ArrayList<HBox> layers;
    private VBox frame;
    private int layerCount;
    private HBox plusSignLayer;
    private HBox layer1;
    private HBox layer2;
    private HBox layer3;
    private Refraction refraction;

    public LayerChoosingController(RefractionView refractionView, Stage stage, ArrayList<HBox> layers, VBox frame, int layerCount, HBox plusSignLayer, Refraction refraction) {
        this.refractionView = refractionView;
        this.stage = stage;
        this.layers = layers;
        this.frame = frame;
        this.layerCount = layerCount;
        this.plusSignLayer = plusSignLayer;
        this.refraction = refraction;

        // retrieve all layers
        layer1 = layers.get(0);
        layer2 = layers.get(1);
        layer3 = layers.get(2);
    }

    private void updateLayer(HBox layer, Color color, String text) {
        layer.getChildren().clear();
        layer.setBackground(Background.fill(color));
        layer.getChildren().add(new Label(text));
    }

    public void onAirPressed() {
        handleMaterialSelection(Material.AIR, "#FFFFFF", "Air, n = 1.0003");
    }

    public void onWaterPressed() {
        handleMaterialSelection(Material.WATER, "#00CDFF", "Water, n = 1.33");
    }

    public void onIcePressed() {
        handleMaterialSelection(Material.ICE, "#E8F8FF", "Ice, n = 1.31");
    }

    public void onRockSaltPressed() {
        handleMaterialSelection(Material.ROCK_SALT, "#FFD8D1", "Rock salt, n = 1.54");
    }

    public void onDiamondPressed() {
        handleMaterialSelection(Material.DIAMOND, "#E6F1FF", "Diamond, n = 2.42");
    }

    public void onGlassPressed() {
        handleMaterialSelection(Material.GLASS, "#F0F8FF", "Glass, n = 1.52");
    }

    private void handleMaterialSelection(Material material, String colorHex, String labelText) {
        refractionView.setChosenLayer(material);

        switch (layerCount) {
            case 0:
                updateLayer(layer1, Color.web(colorHex), labelText);
                frame.getChildren().clear();
                frame.getChildren().addAll(layer1, plusSignLayer);
                refraction.setLayer1(layer1);
                break;
            case 1:
                updateLayer(layer2, Color.web(colorHex), labelText);
                frame.getChildren().clear();
                frame.getChildren().addAll(layer1, layer2, plusSignLayer, refractionView.getObject());
                refraction.setLayer2(layer2);
                break;
            case 2:
                updateLayer(layer3, Color.web(colorHex), labelText);
                frame.getChildren().clear();
                frame.getChildren().addAll(layer1, layer2, layer3, refractionView.getObject());
                refraction.setLayer3(layer3);
        }

        stage.close();
        refraction.setLayerCount(refraction.getLayerCount() + 1);
    }
}
