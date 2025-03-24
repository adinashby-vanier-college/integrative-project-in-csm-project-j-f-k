package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.optics.jfkt.MainApp;
import project.optics.jfkt.views.LayerChoosingView;
import project.optics.jfkt.views.RefractionView;

import java.util.ArrayList;

public class RefractionController {
    public void onNewLayerButtonPressed(RefractionView refractionView, ArrayList<HBox> layers, VBox frame, int currentLayer, HBox plusSignLayer, Line line12, Line line23) {
        Stage stage = new Stage();
        stage.initOwner(MainApp.primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false); // Disable resizing
        stage.initStyle(StageStyle.UTILITY); // Hide minimize/maximize buttons
        stage.setTitle("Material selection");
        stage.setScene(new Scene(new LayerChoosingView(refractionView, stage, layers, frame, currentLayer, plusSignLayer, line12, line23)));
        stage.setFullScreen(false);
        stage.show();
    }

    public void onIncidentLocationChanged(Number incidentLocation, Circle incidentPoint) {
        incidentPoint.setCenterX(incidentLocation.doubleValue());
    }
}
