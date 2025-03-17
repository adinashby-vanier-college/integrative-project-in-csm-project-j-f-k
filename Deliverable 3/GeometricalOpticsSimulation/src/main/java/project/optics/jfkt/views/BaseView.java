package project.optics.jfkt.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import project.optics.jfkt.utils.Util;


public class BaseView extends BorderPane{
    Util util = new Util();
    private String type;
    private String option1;
    private String option2;

    public BaseView(String type){
        if (type.contentEquals("Mirrors")){
            this.type = type;
            option1 = "Convex";
            option2 = "Concave";
        } else{
            this.type = type;
            option1 = "Converging";
            option2 = "Diverging";
        }
        this.setTop(util.createMenu());
        this.setCenter(createCenter());
        this.setBottom(createBottom());
    }

    Region createCenter(){
        Pane mainpane = new Pane();
        mainpane.setPrefSize(1920,1080);

        VBox paramvbox = new VBox();
        Text paramheadertext = new Text();
        Font paramheaderfont = new Font(40);
        paramheadertext.setText("Parameters:");
        paramheadertext.setFont(paramheaderfont);
        paramheadertext.setTextAlignment(TextAlignment.CENTER);
        paramheadertext.setUnderline(true);
        paramvbox.setPrefSize(420,720);
        paramvbox.getChildren().add(paramheadertext);
        paramvbox.getChildren().addAll(createParamHbox("Focal Length"),createParamHbox("Image Distance"),createParamHbox("Image Height"));


        Pane animpane = new Pane();
        Text animtext = new Text();
        HBox zoomhbox =new HBox();
        ImageView zoominImage = new ImageView(new Image(this.getClass().getResource("/images/64/Magnifying-Glass-Add.png").toExternalForm()));
        ImageView zoomoutImage = new ImageView(new Image(this.getClass().getResource("/images/64/Magnifying-Glass-Reduce.png").toExternalForm()));
        Button zoomin = new Button("", zoominImage);
        Button zoomout = new Button("", zoomoutImage);
        Button backmenu = new Button("Back To Main Menu");
        backmenu.setPrefSize(150, 50);
        animpane.setPrefSize(1500,720);
        animpane.setLayoutX(420);
        animpane.setStyle("-fx-border-color: black; -fx-border-width: 4px;");
        animtext.setText("Animation View");
        animtext.setLayoutX(750);
        animtext.setLayoutY(360);
        zoomhbox.setPrefSize(200,100);
        zoomhbox.setLayoutX(4);
        zoomhbox.setLayoutY(4);
        zoomhbox.getChildren().addAll(zoomin,zoomout,backmenu);
        animpane.getChildren().addAll(animtext,zoomhbox);




        mainpane.getChildren().addAll(paramvbox,animpane);

        return mainpane;
    }

    private Region createBottom(){
        HBox mainhbox = new HBox();
        mainhbox.setAlignment(Pos.CENTER);
        mainhbox.setSpacing(30);

        VBox animbuttonvbox = new VBox();
        animbuttonvbox.setPrefSize(380,280);
        animbuttonvbox.setAlignment(Pos.CENTER);
        animbuttonvbox.setSpacing(20);

        HBox topbuttons = new HBox();
        topbuttons.setAlignment(Pos.CENTER);

        ImageView playimage = new ImageView(new Image(this.getClass().getResource("/images/64/Play.png").toExternalForm()));
        Button playbutton = new Button("",playimage);
        playbutton.setPrefSize(120,20);

        ImageView pauseimage = new ImageView(new Image(this.getClass().getResource("/images/64/Pause.png").toExternalForm()));
        Button pausebutton = new Button("",pauseimage);
        pausebutton.setPrefSize(120,20);

        ImageView redoimage = new ImageView(new Image(this.getClass().getResource("/images/64/Redo.png").toExternalForm()));
        Button redobutton = new Button("",redoimage);
        redobutton.setPrefSize(120,20);

        topbuttons.getChildren().addAll(playbutton,pausebutton,redobutton);

        HBox bottombuttons = new HBox();
        bottombuttons.setAlignment(Pos.CENTER);

        ImageView slowimage = new ImageView(new Image(this.getClass().getResource("/images/64/Double-Chevron-Arrow-Left.png").toExternalForm()));
        Button slowbutton = new Button("",slowimage);
        slowbutton.setPrefSize(98,20);

        ImageView normalimage = new ImageView(new Image(this.getClass().getResource("/images/64/Next.png").toExternalForm()));
        Button normalbutton = new Button("",normalimage);
        normalbutton.setPrefSize(98,20);

        ImageView fastimage = new ImageView(new Image(this.getClass().getResource("/images/64/Double-Chevron-Arrow-Right.png").toExternalForm()));
        Button fastbutton = new Button("",fastimage);
        fastbutton.setPrefSize(98,20);

        bottombuttons.getChildren().addAll(slowbutton,normalbutton,fastbutton);

        animbuttonvbox.getChildren().addAll(topbuttons,bottombuttons);

        Pane choicepane = new Pane();
        Text choicetext = new Text();
        Font font1 = new Font(36);
        Font font2 = new Font(28);
        choicetext.setText(type);
        choicetext.setFont(font1);
        choicetext.setY(40);
        HBox choicehbox = new HBox();
        choicehbox.setAlignment(Pos.CENTER);
        Pane optionpane1 = new Pane();
        Text optiontext1 = new Text();
        Button optionbutton1 = new Button("", null);
        Pane optionpane2 = new Pane();
        Text optiontext2 = new Text();
        Button optionbutton2 = new Button("", null);

        optionpane1.setPrefSize(160,160);
        optiontext1.setText(option1);
        optiontext1.setFont(font2);
        optiontext1.setLayoutX(100-optiontext1.getBoundsInLocal().getWidth());
        optiontext1.setLayoutY(260);
        optionbutton1.setPrefSize(80,180);
        optionbutton1.setLayoutY(50);
        optionpane1.getChildren().addAll(optiontext1,optionbutton1);

        optionpane2.setPrefSize(200,160);
        optiontext2.setText(option2);
        optiontext2.setFont(font2);
        optiontext2.setLayoutX(100-optiontext2.getBoundsInLocal().getWidth());
        optiontext2.setLayoutY(260);
        optionbutton2.setPrefSize(80,180);
        optionbutton2.setLayoutY(50);
        optionpane2.getChildren().addAll(optiontext2,optionbutton2);

        choicehbox.getChildren().addAll(optionpane1, optionpane2);
        choicehbox.setLayoutX(choicetext.getBoundsInLocal().getWidth());
        choicepane.getChildren().addAll(choicetext,choicehbox);




        mainhbox.getChildren().addAll(animbuttonvbox, choicepane);

        return  mainhbox;

    }

    HBox createParamHbox(String Text){
        HBox paramhbox = new HBox();
        paramhbox.setSpacing(40);
        paramhbox.setPrefSize(400,100);
        paramhbox.setAlignment(Pos.CENTER);

        Text paramtext = new Text();
        Font textfont = new Font(30);
        paramtext.setText(Text);
        paramtext.setFont(textfont);

        paramhbox.getChildren().add(paramtext);

        TextField paramtextfield = new TextField();
        Font fieldfont = new Font(28);
        paramtextfield.setAlignment(Pos.CENTER);
        paramtextfield.setPrefSize(160,60);
        paramtextfield.setFont(fieldfont);

        paramhbox.getChildren().add(paramtextfield);


        return paramhbox;
    }
}
