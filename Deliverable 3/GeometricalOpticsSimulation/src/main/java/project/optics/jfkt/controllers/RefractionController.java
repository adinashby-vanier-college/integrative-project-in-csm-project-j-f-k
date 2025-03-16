package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.views.LayerChoosingView;
import project.optics.jfkt.views.RefractionView;

import java.util.ArrayList;

public class RefractionController {
    public void onNewLayerButtonPressed(RefractionView refractionView, ArrayList<HBox> layers, VBox frame, int currentLayer, HBox plusSignLayer) {
        Stage stage = new Stage();
        stage.initOwner(MainApp.primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false); // Disable resizing
        stage.initStyle(StageStyle.UTILITY); // Hide minimize/maximize buttons
        stage.setTitle("Material selection");
        stage.setScene(new Scene(new LayerChoosingView(refractionView, stage, layers, frame, currentLayer, plusSignLayer)));
        stage.setFullScreen(false);
        stage.show();
    }
}
