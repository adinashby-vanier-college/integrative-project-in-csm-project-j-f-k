package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;

public class EducationModeView extends BorderPane {
    private final Util util = new Util();
    private Difficulty difficulty;
    private ArrayList<Question> questionBank;

    public EducationModeView(Difficulty difficulty) {
        this.difficulty = difficulty;
        // Top: menu bar
        this.setTop(createTop());
        // Center: main question area with top and bottom buttons
        this.setCenter(createCenter());
        // Right: parameters and answer controls
        this.setRight(createRight());
    }

    private Region createTop() {
        return util.createMenu();
    }

    private Region createCenter() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        // Border for debugging
        container.setBorder(Border.stroke(Color.BLACK));

        // Top buttons for zoom/back + hint area
        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(50);
        HBox.setMargin(topButtons, new Insets(10, 0, 0, 0));

        // Hint area
        StackPane hintArea = new StackPane();
        hintArea.setPrefSize(300, 100);
        hintArea.setBorder(Border.stroke(Color.BLACK));
        Text hint = new Text("hint test");
        hint.setWrappingWidth(280);  // 300px width - 10px padding each side
        hintArea.setPadding(new Insets(5));  // Add internal padding
        hintArea.getChildren().add(hint);
        HBox.setMargin(hintArea, new Insets(10, 0, 0, 0));

        // Answer area
        StackPane answerArea = new StackPane();
        answerArea.setPrefSize(300, 100);
        answerArea.setBorder(Border.stroke(Color.BLACK));
        Text answer = new Text("Answer test");
        answer.setWrappingWidth(280);
        answerArea.setPadding(new Insets(5));
        answerArea.getChildren().add(answer);
        HBox.setMargin(answerArea, new Insets(10, 0, 0, 0));

        HBox topRegion = new HBox(300, topButtons, hintArea, answerArea);
        // The question pane
        Region questionPane = createQuestionPane();
        VBox.setVgrow(questionPane, Priority.ALWAYS);

        // Bottom buttons
        HBox bottomButtons = new HBox(20);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(10));

        Button submit = new Button("Submit");
        Button hintButton = new Button("Hint");
        Button answerButton = new Button("Answer");

        submit.setPrefWidth(100);
        hintButton.setPrefWidth(100);
        answerButton.setPrefWidth(100);
        bottomButtons.getChildren().addAll(submit, hintButton, answerButton);

        container.getChildren().addAll(topRegion, questionPane, bottomButtons);
        return container;
    }

    private Region createQuestionPane() {
        BorderPane questionPane = new BorderPane();
        questionPane.setPadding(new Insets(20));
        questionPane.setBorder(Border.stroke(Color.BLACK));

        Text question = new Text("Question 123");
        questionPane.setTop(question);

        // Image display pane
        Pane imagePane = new Pane();
        imagePane.setPrefSize(300, 200);
        imagePane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: rgb(128,128,128);");
        questionPane.setCenter(imagePane);

        return questionPane;
    }

    private Region createRight() {
        VBox container = new VBox(100);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        container.setPrefWidth(250);

        // create options for answer
        ToggleGroup groupMode = new ToggleGroup();
        Node virtualOption = createCustomRadioOption("Virtual", groupMode);
        Node realOption = createCustomRadioOption("Real", groupMode);
        VBox modeGroup = new VBox(10, virtualOption, realOption); // little spacing between options
        modeGroup.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup groupSize = new ToggleGroup();
        Node biggerOption = createCustomRadioOption("Bigger", groupSize);
        Node smallerOption = createCustomRadioOption("Smaller", groupSize);
        VBox sizeGroup = new VBox(10, biggerOption, smallerOption);
        sizeGroup.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup groupOrientation = new ToggleGroup();
        Node uprightOption = createCustomRadioOption("Upright", groupOrientation);
        Node invertedOption = createCustomRadioOption("Inverted", groupOrientation);
        VBox orientationGroup = new VBox(10, uprightOption, invertedOption);
        orientationGroup.setAlignment(Pos.CENTER_LEFT);

        // TextField for user input + new question button
        TextField userInputField = new TextField();
        userInputField.setPromptText("User Input");
        userInputField.setMaxWidth(Double.MAX_VALUE);

        Button newQuestion = new Button("New");
        newQuestion.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(modeGroup, sizeGroup, orientationGroup, userInputField, newQuestion);
        return container;
    }

    /**
     * Creates custom radiobox where the text is on the left and
     * a square box on the right
     */
    private Node createCustomRadioOption(String text, ToggleGroup group) {
        Label label = new Label(text);

        RadioButton rb = new RadioButton();
        rb.setToggleGroup(group);
        // Hide the default radio circle.
        rb.setStyle("-fx-opacity: 0; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");

        // Create the square
        Rectangle square = new Rectangle(15, 15);
        square.setStroke(Color.BLACK);
        square.setFill(Color.TRANSPARENT);
        rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            square.setFill(isSelected ? Color.BLACK : Color.TRANSPARENT);
        });


        HBox optionContainer = new HBox(10);
        optionContainer.setAlignment(Pos.CENTER_LEFT);
        // Add a spacer between label and square so that the label stays left.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        optionContainer.getChildren().addAll(label, spacer, square);

        // Make the entire container clickable.
        optionContainer.setOnMouseClicked(e -> rb.setSelected(true));
        // Add the invisible radio button to the container.
        optionContainer.getChildren().add(rb);
        rb.setManaged(false);
        rb.setVisible(false);

        return optionContainer;
    }
}
