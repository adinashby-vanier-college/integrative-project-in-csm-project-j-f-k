package project.optics.jfkt.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LensView extends BaseView {

    public LensView() {
        super("Lenses");
    }
    @Override
    protected Pane createCenter() {
        Pane baseCenter = (Pane) super.createCenter(); // Get BaseView’s center layout

        // Find the VBox containing parameters
        VBox paramVBox = null;
        for (Node node : baseCenter.getChildren()) {
            if (node instanceof VBox) {
                paramVBox = (VBox) node;
                break;
            }
        }

        // If found, add extra parameters
        if (paramVBox != null) {
            paramVBox.getChildren().addAll(
                    createParamHbox("Magnification"),
                    createParamHbox("Number of Rays")
            );
        }

        return baseCenter;


    }


}
