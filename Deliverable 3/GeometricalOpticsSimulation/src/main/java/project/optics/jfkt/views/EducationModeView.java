package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import project.optics.jfkt.utils.Util;

public class EducationModeView extends BorderPane {
    private final Util util = new Util();

    public EducationModeView() {
        this.setTop(util.createMenu());
        this.setCenter(createCenter());
        this.setRight(createRight());
    }

    private Region createCenter() {
        VBox container = new VBox(50);
        container.setAlignment(Pos.CENTER);
        container.setBorder(Border.stroke(Color.BLACK));

        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setBorder(Border.stroke(Color.BLACK));
        topButtons.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(topButtons, new Insets(50, 0, 0, 50));

        HBox bottomButtons = new HBox(50);
        bottomButtons.setAlignment(Pos.BOTTOM_RIGHT);
        bottomButtons.setBorder(Border.stroke(Color.BLACK));

        Button submit = new Button("Submit");
        Button hint = new Button("Hint");
        Button answer = new Button("Answer");

        bottomButtons.getChildren().addAll(submit, hint, answer);

        container.getChildren().addAll(topButtons, createQuestionPane(), bottomButtons);
        return container;
    }

    private Region createQuestionPane() {
        HBox container = new HBox(50);

        Text question = new Text("123");
        Pane animationPane = new Pane();

        container.getChildren().addAll(question, animationPane);
        return container;
    }

    private Region createRight() {
        VBox container = new VBox(30);

        RadioButton virtual = new RadioButton("Virtual");
        virtual.setSelected(true);
        RadioButton real = new RadioButton("Real");

        ToggleGroup group1 = new ToggleGroup();
        virtual.setToggleGroup(group1);
        real.setToggleGroup(group1);

        RadioButton bigger = new RadioButton("Bigger");
        bigger.setSelected(true);
        RadioButton smaller = new RadioButton("Smaller");

        ToggleGroup group2 = new ToggleGroup();
        bigger.setToggleGroup(group2);
        smaller.setToggleGroup(group2);

        RadioButton upright = new RadioButton("Upright");
        upright.setSelected(true);
        RadioButton inverted = new RadioButton("Inverted");

        ToggleGroup group3 = new ToggleGroup();
        upright.setToggleGroup(group3);
        inverted.setToggleGroup(group3);

        // create user input answer field
        TextField answer = new TextField();

        Button newQuestion = new Button("New");

        container.getChildren().addAll(virtual, real, bigger, smaller, upright, inverted, answer, newQuestion);
        return container;
    }


}
