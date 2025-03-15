package project.optics.jfkt.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LensView extends BorderPane {
Parent root;

    public LensView() throws IOException {
        this.setCenter(FXMLLoader.load(getClass().getResource("/fxml/lensuifxml.fxml")));
    }

}
