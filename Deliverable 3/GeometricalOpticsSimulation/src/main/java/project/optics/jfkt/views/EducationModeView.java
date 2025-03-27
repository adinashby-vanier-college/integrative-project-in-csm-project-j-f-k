package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import project.optics.jfkt.controllers.EducationModeController;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.utils.Util;

import java.util.ArrayList;

public class EducationModeView extends BorderPane {
    private final Util util = new Util();
    private Difficulty difficulty;
    private ArrayList<Question> questionBank;

    // UI elements stored as fields
    private Text questionText;
    private Text hintText;
    private Text answerText;
    private TextField userInputField;
    private Button submitButton;
    private Button hintButton;
    private Button answerButton;
    private Button newQuestionButton;

    public EducationModeView(Difficulty difficulty) {
        this.difficulty = difficulty;
        // Initialize UI components
        this.setTop(createTop());
        this.setCenter(createCenter());
        this.setRight(createRight());

        // Initialize controller
        new EducationModeController(this, difficulty);
    }

    private Region createTop() {
        return util.createMenu();
    }

    private Region createCenter() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setBorder(Border.stroke(Color.BLACK));

        // Top buttons and hint/answer areas
        HBox topButtons = util.createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(50);
        HBox.setMargin(topButtons, new Insets(10, 0, 0, 0));

        // Hint area
        StackPane hintArea = new StackPane();
        hintArea.setPrefSize(300, 100);
        hintArea.setBorder(Border.stroke(Color.BLACK));
        hintText = new Text();
        hintText.setWrappingWidth(280);
        hintArea.setPadding(new Insets(5));
        hintArea.getChildren().add(hintText);

        // Answer area
        StackPane answerArea = new StackPane();
        answerArea.setPrefSize(300, 100);
        answerArea.setBorder(Border.stroke(Color.BLACK));
        answerText = new Text();
        answerText.setWrappingWidth(280);
        answerArea.setPadding(new Insets(5));
        answerArea.getChildren().add(answerText);

        HBox topRegion = new HBox(300, topButtons, hintArea, answerArea);

        // Question pane
        Region questionPane = createQuestionPane();
        VBox.setVgrow(questionPane, Priority.ALWAYS);

        // Bottom buttons
        HBox bottomButtons = new HBox(20);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(10));

        submitButton = new Button("Submit");
        hintButton = new Button("Hint");
        answerButton = new Button("Answer");

        submitButton.setPrefWidth(100);
        hintButton.setPrefWidth(100);
        answerButton.setPrefWidth(100);
        bottomButtons.getChildren().addAll(submitButton, hintButton, answerButton);

        container.getChildren().addAll(topRegion, questionPane, bottomButtons);
        return container;
    }

    private Region createQuestionPane() {
        BorderPane questionPane = new BorderPane();
        questionPane.setPadding(new Insets(20));
        questionPane.setBorder(Border.stroke(Color.BLACK));

        questionText = new Text("Select 'New' to get a question");
        questionPane.setTop(questionText);

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

        // Radio button groups
        ToggleGroup groupMode = new ToggleGroup();
        Node virtualOption = createCustomRadioOption("Virtual", groupMode);
        Node realOption = createCustomRadioOption("Real", groupMode);
        VBox modeGroup = new VBox(10, virtualOption, realOption);

        ToggleGroup groupSize = new ToggleGroup();
        Node biggerOption = createCustomRadioOption("Bigger", groupSize);
        Node smallerOption = createCustomRadioOption("Smaller", groupSize);
        VBox sizeGroup = new VBox(10, biggerOption, smallerOption);

        ToggleGroup groupOrientation = new ToggleGroup();
        Node uprightOption = createCustomRadioOption("Upright", groupOrientation);
        Node invertedOption = createCustomRadioOption("Inverted", groupOrientation);
        VBox orientationGroup = new VBox(10, uprightOption, invertedOption);

        // User input and new question button
        userInputField = new TextField();
        userInputField.setPromptText("Enter your answer");
        userInputField.setMaxWidth(Double.MAX_VALUE);

        newQuestionButton = new Button("New Question");
        newQuestionButton.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(modeGroup, sizeGroup, orientationGroup, userInputField, newQuestionButton);
        return container;
    }

    private Node createCustomRadioOption(String text, ToggleGroup group) {
        Label label = new Label(text);
        RadioButton rb = new RadioButton();
        rb.setToggleGroup(group);
        rb.setStyle("-fx-opacity: 0; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");

        Rectangle square = new Rectangle(15, 15);
        square.setStroke(Color.BLACK);
        square.setFill(Color.TRANSPARENT);
        rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            square.setFill(isSelected ? Color.BLACK : Color.TRANSPARENT);
        });

        HBox optionContainer = new HBox(10);
        optionContainer.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        optionContainer.getChildren().addAll(label, spacer, square, rb);
        rb.setManaged(false);
        rb.setVisible(false);
        optionContainer.setOnMouseClicked(e -> rb.setSelected(true));

        return optionContainer;
    }

    // Getters for UI elements
    public Text getQuestionText() { return questionText; }
    public Text getHintText() { return hintText; }
    public Text getAnswerText() { return answerText; }
    public TextField getUserInputField() { return userInputField; }
    public Button getSubmitButton() { return submitButton; }
    public Button getHintButton() { return hintButton; }
    public Button getAnswerButton() { return answerButton; }
    public Button getNewQuestionButton() { return newQuestionButton; }
}