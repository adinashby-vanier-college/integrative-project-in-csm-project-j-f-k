package project.optics.jfkt.views;


import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BaseView {
    @FXML
    Pane Animpane;
    @FXML
    HBox paramtemplate;
    @FXML
    VBox paramvbox;

    void quitaction(){
        System.out.println("Quit button activated");
        Stage stage = (Stage) Animpane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void settingsaction(){
        System.out.println("Settings button activated");
    }

    @FXML
    void helpaction(){
        System.out.println("Help button activated");
    }

    @FXML
    void aboutusaction(){
        System.out.println("About us button activated");
    }
    
    @FXML
    void backtomenuaction(){
        System.out.println("About us button activated");
    }

    void paramadd(String Text){
        HBox hboxclone = new HBox();
        hboxclone.setSpacing(paramtemplate.getSpacing());
        paramtemplate.getChildren().forEach(node -> {
            if (node instanceof Text){
                Text newtext = new Text(((Text) node).getText());
                newtext.setText(Text);
                hboxclone.getChildren().add(newtext);
            }
            if (node instanceof TextField){
                TextField newtextfield = new TextField();

                newtextfield.setPrefSize(((TextField) node).getPrefWidth(),((TextField) node).getPrefHeight());
                hboxclone.getChildren().add(newtextfield);
            }
        });
        paramvbox.getChildren().add(hboxclone);
    }

    void paramchange(String Text){
        paramtemplate.getChildren().forEach(node -> {
            if (node instanceof  Text){
                ((Text) node).setText(Text);
            }
        });
    }
}
