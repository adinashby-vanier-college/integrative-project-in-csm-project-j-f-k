package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import project.optics.jfkt.controllers.BaseViewController;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class BaseView extends BorderPane {
    Util util = new Util();
    private String type;
    private String option1;
    private String option2;
    private Pane animpane;
    private BaseViewController baseViewController;
    private Button optionbutton1;
    private Button optionbutton2;
    private Button playbutton;
    private Button pausebutton;
    private Button redobutton;
    private ArrayList<TextField> textInputs = new ArrayList<>();
    private Button slowbutton;
    private Button normalbutton;
    private Button fastbutton;
    private HBox zoomhbox;
    private Button zoomin;
    private Button zoomout;
    private VBox paramVBox;
    private Text parametersHeader;
    private final List<Text> parameterLabels = new ArrayList<>();
    private Text rayLengthLabel;
    private List<Label> lensLabels = new ArrayList<>();
    private List<Text> focalLabels = new ArrayList<>();

    public BaseView(String type) {
        if (type.contentEquals("Mirrors")) {
            this.type = type;
            option1 = "Concave";
            option2 = "Convex";
        } else {
            this.type = type;
            option1 = "Converging";
            option2 = "Diverging";
        }
        baseViewController = new BaseViewController();
        this.setTop(util.createMenu());
        this.setCenter(createCenter());
        this.setBottom(createBottom());
        updateFonts(ThemeController.getCurrentFont());
    }
    private void updateFonts(String font) {
        String fontStyle = "-fx-font-family: '" + font + "';";
        if (paramVBox != null) {
            paramVBox.lookupAll(".parameter-label").forEach(node -> {
                if (node instanceof Text) {
                    ((Text)node).setStyle(fontStyle);
                }
            });
        }
        if (parametersHeader != null) parametersHeader.setStyle(fontStyle);
        for (Text label : parameterLabels) {
            label.setStyle(fontStyle);
        }
        if (rayLengthLabel != null) rayLengthLabel.setStyle(fontStyle);
        for (Label label : lensLabels) {
            label.setStyle(fontStyle);
        }
        for (Text label : focalLabels) {
            label.setStyle(fontStyle);
        }
    }
    Region createCenter() {
        HBox mainpane = new HBox();
        mainpane.setPrefSize(1920, 1080);

        VBox paramvbox = new VBox();
        parametersHeader = new Text(GeneralSetting.getString("text.parameters"));
        parametersHeader.setFont(new Font(40));
        parametersHeader.setTextAlignment(TextAlignment.CENTER);
        parametersHeader.setUnderline(true);
        parametersHeader.getStyleClass().add("lensview-parameters-header");

        parametersHeader.setUnderline(true);
        paramvbox.setPrefSize(420, 720);
        paramvbox.getChildren().add(parametersHeader);
        paramvbox.getChildren().addAll(createParamHbox(GeneralSetting.getString("text.focalLength")), createParamHbox(GeneralSetting.getString("text.objectDistance")), createParamHbox(GeneralSetting.getString("text.objectHeight")));

        animpane = new Pane();
        animpane.getStyleClass().add("animation-pane");
        zoomhbox = new HBox();
        zoomhbox.setViewOrder(-1);
        ImageView zoominImage = new ImageView(new Image(this.getClass().getResource("/images/64/Magnifying-Glass-Add.png").toExternalForm()));
        ImageView zoomoutImage = new ImageView(new Image(this.getClass().getResource("/images/64/Magnifying-Glass-Reduce.png").toExternalForm()));
        zoomin = new Button("", zoominImage);
        zoomout = new Button("", zoomoutImage);
        animpane.setPrefSize(1400, 720);
        animpane.setPrefWidth(1500);
        animpane.setStyle("-fx-border-color: black; -fx-border-width: 4px;");

        //this is so the animation pane doesn't "leave" the black border
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(animpane.widthProperty());
        clip.heightProperty().bind(animpane.heightProperty());
        animpane.setClip(clip);

        Line opticalAxis = new Line(0, animpane.getPrefHeight() / 2, animpane.getPrefWidth(), animpane.getPrefHeight() / 2);
        opticalAxis.getStyleClass().add("optical-axis");
        opticalAxis.setStroke(Color.BLACK);
        opticalAxis.setStrokeWidth(1);
        zoomhbox.setPrefSize(200, 100);
        zoomhbox.setLayoutX(4);
        zoomhbox.setLayoutY(4);
        zoomhbox.getChildren().addAll(zoomin, zoomout);
        animpane.getChildren().addAll(opticalAxis, zoomhbox);

        mainpane.getChildren().addAll(paramvbox, animpane);
        return mainpane;
    }

    private Region createBottom() {
        HBox mainhbox = new HBox();
        mainhbox.setAlignment(Pos.CENTER);
        mainhbox.setSpacing(80);

        VBox animbuttonvbox = new VBox();
        animbuttonvbox.setPrefSize(380, 280);
        animbuttonvbox.setAlignment(Pos.CENTER);
        animbuttonvbox.setSpacing(20);

        HBox topbuttons = new HBox();
        topbuttons.setAlignment(Pos.CENTER);

        ImageView playimage = new ImageView(new Image(this.getClass().getResource("/images/64/Play.png").toExternalForm()));
        playbutton = new Button("", playimage);
        playbutton.setPrefSize(120, 20);

        ImageView pauseimage = new ImageView(new Image(this.getClass().getResource("/images/64/Pause.png").toExternalForm()));
        pausebutton = new Button("", pauseimage);
        pausebutton.setPrefSize(120, 20);

        ImageView redoimage = new ImageView(new Image(this.getClass().getResource("/images/64/Redo.png").toExternalForm()));
        redobutton = new Button("", redoimage);
        redobutton.setPrefSize(120, 20);

        topbuttons.getChildren().addAll(playbutton, pausebutton, redobutton);

        HBox bottombuttons = new HBox();
        bottombuttons.setAlignment(Pos.CENTER);

        ImageView slowimage = new ImageView(new Image(this.getClass().getResource("/images/64/Double-Chevron-Arrow-Left.png").toExternalForm()));
        slowbutton = new Button("", slowimage);
        slowbutton.setPrefSize(98, 20);

        ImageView normalimage = new ImageView(new Image(this.getClass().getResource("/images/64/Next.png").toExternalForm()));
        normalbutton = new Button("", normalimage);
        normalbutton.setPrefSize(98, 20);

        ImageView fastimage = new ImageView(new Image(this.getClass().getResource("/images/64/Double-Chevron-Arrow-Right.png").toExternalForm()));
        fastbutton = new Button("", fastimage);
        fastbutton.setPrefSize(98, 20);

        bottombuttons.getChildren().addAll(slowbutton, normalbutton, fastbutton);

        animbuttonvbox.getChildren().addAll(topbuttons, bottombuttons);

        Pane choicepane = new Pane();
        Text choicetext = new Text();
        choicetext.getStyleClass().add("lensview-ray-length-label");
        Font font1 = new Font(36);
        Font font2 = new Font(28);
        choicetext.setText(type);
        choicetext.setFont(font1);
        choicetext.setY(40);
        HBox choicehbox = new HBox();
        choicehbox.setAlignment(Pos.CENTER);
        Pane optionpane1 = new Pane();
        Text optiontext1 = new Text();
        optiontext1.getStyleClass().add("lensview-ray-length-label");
        optionbutton1 = new Button("", null);
        Pane optionpane2 = new Pane();
        Text optiontext2 = new Text();
        optiontext2.getStyleClass().add("lensview-ray-length-label");
        optionbutton2 = new Button("", null);

        optionpane1.setPrefSize(160, 160);
        optiontext1.setText(option1);
        optiontext1.setFont(font2);
        optiontext1.setLayoutX(100 - optiontext1.getBoundsInLocal().getWidth());
        optiontext1.setLayoutY(260);
        optionbutton1.setPrefSize(80, 180);
        optionbutton1.setLayoutY(50);
        optionpane1.getChildren().addAll(optiontext1, optionbutton1);

        optionpane2.setPrefSize(200, 160);
        optiontext2.setText(option2);
        optiontext2.setFont(font2);
        optiontext2.setLayoutX(100 - optiontext2.getBoundsInLocal().getWidth());
        optiontext2.setLayoutY(260);
        optionbutton2.setPrefSize(80, 180);
        optionbutton2.setLayoutY(50);
        optionpane2.getChildren().addAll(optiontext2, optionbutton2);

        choicehbox.getChildren().addAll(optionpane1, optionpane2);
        choicehbox.setLayoutX(choicetext.getBoundsInLocal().getWidth());
        choicepane.getChildren().addAll(choicetext, choicehbox);

        Button backmenu = new Button(GeneralSetting.getString("button.back"));
        backmenu.setPrefSize(150, 50);
        backmenu.setPadding(new Insets(20));
        backmenu.setOnAction(e -> {
            baseViewController.onBackButtonPressed();
        });


        mainhbox.getChildren().addAll(backmenu, animbuttonvbox, choicepane);

        return mainhbox;

    }

    HBox createParamHbox(String Text) {
        HBox paramhbox = new HBox();
        paramhbox.setSpacing(40);
        paramhbox.setPrefSize(400, 100);
        paramhbox.setAlignment(Pos.CENTER);

        Text paramtext = new Text();
        paramtext.getStyleClass().add("lensview-ray-length-label");
        paramtext.setText(Text);


        paramhbox.getChildren().add(paramtext);

        TextField paramtextfield = new TextField();
        Font fieldfont = new Font(28);
        paramtextfield.setAlignment(Pos.CENTER);
        paramtextfield.setPrefSize(160, 60);
        paramtextfield.setFont(fieldfont);
        textInputs.add(paramtextfield);

        paramhbox.getChildren().add(paramtextfield);


        return paramhbox;
    }


    public Pane getAnimpane() {
        return animpane;
    }

    public Button getOptionbutton1() {
        return optionbutton1;
    }

    public Button getOptionbutton2() {
        return optionbutton2;
    }

    public ArrayList<TextField> getTextInputs() {
        return textInputs;
    }

    public Button getPlaybutton() {
        return playbutton;
    }

    public Button getPausebutton() {
        return pausebutton;
    }

    public Button getRedobutton() {
        return redobutton;
    }

    public Button getSlowbutton() {
        return slowbutton;
    }

    public Button getFastbutton() {
        return fastbutton;
    }

    public Button getNormalbutton() {
        return normalbutton;
    }

    public HBox getZoomhbox() {
        return zoomhbox;
    }

    public Button getZoomin() {
        return zoomin;
    }

    public Button getZoomout() {
        return zoomout;
    }
}
