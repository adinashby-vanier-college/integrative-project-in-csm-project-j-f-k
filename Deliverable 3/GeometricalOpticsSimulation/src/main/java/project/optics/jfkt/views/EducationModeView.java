package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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

    // UI elements
    private Text questionText;
    private Text hintText;
    private Text answerText;
    private TextField userInputField;
    private Button submitButton;
    private Button hintButton;
    private Button answerButton;
    private Button newQuestionButton;

    // Radio button groups
    private ToggleGroup modeGroup;
    private ToggleGroup sizeGroup;
    private ToggleGroup orientationGroup;

    // Font sizes
    private static final double QUESTION_FONT_SIZE = 18.0;
    private static final double HINT_FONT_SIZE = 16.0;
    private static final double ANSWER_FONT_SIZE = 16.0;
    private static final double INPUT_FONT_SIZE = 14.0;

    public EducationModeView(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.setTop(createTop());
        this.setCenter(createCenter());
        this.setRight(createRight());
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
        hintText.setFont(new Font(HINT_FONT_SIZE));
        hintText.setWrappingWidth(280);
        hintArea.setPadding(new Insets(5));
        hintArea.getChildren().add(hintText);

        // Answer area
        StackPane answerArea = new StackPane();
        answerArea.setPrefSize(300, 100);
        answerArea.setBorder(Border.stroke(Color.BLACK));
        answerText = new Text();
        answerText.setFont(new Font(ANSWER_FONT_SIZE));
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
        questionText.setFont(new Font(QUESTION_FONT_SIZE));
        questionPane.setTop(questionText);

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
        modeGroup = new ToggleGroup();
        Node virtualOption = createCustomRadioOption("Virtual", modeGroup);
        Node realOption = createCustomRadioOption("Real", modeGroup);
        VBox modeGroupBox = new VBox(10, virtualOption, realOption);

        sizeGroup = new ToggleGroup();
        Node biggerOption = createCustomRadioOption("Bigger", sizeGroup);
        Node smallerOption = createCustomRadioOption("Smaller", sizeGroup);
        VBox sizeGroupBox = new VBox(10, biggerOption, smallerOption);

        orientationGroup = new ToggleGroup();
        Node uprightOption = createCustomRadioOption("Upright", orientationGroup);
        Node invertedOption = createCustomRadioOption("Inverted", orientationGroup);
        VBox orientationGroupBox = new VBox(10, uprightOption, invertedOption);

        // User input and new question button
        userInputField = new TextField();
        userInputField.setFont(new Font(INPUT_FONT_SIZE));
        userInputField.setPromptText("Enter distance answer");
        userInputField.setMaxWidth(Double.MAX_VALUE);

        newQuestionButton = new Button("New Question");
        newQuestionButton.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(modeGroupBox, sizeGroupBox, orientationGroupBox, userInputField, newQuestionButton);
        return container;
    }

    private Node createCustomRadioOption(String text, ToggleGroup group) {
        Label label = new Label(text);
        label.setFont(new Font(INPUT_FONT_SIZE));

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

        // Store the container in the radio button's user data
        rb.setUserData(optionContainer);

        optionContainer.setOnMouseClicked(e -> rb.setSelected(true));
        return optionContainer;
    }

    public void setRadioButtonsEnabled(boolean enabled) {
        modeGroup.getToggles().forEach(toggle -> {
            HBox container = (HBox) toggle.getUserData();
            container.setDisable(!enabled);
            container.setOpacity(enabled ? 1.0 : 0.5);
        });

        sizeGroup.getToggles().forEach(toggle -> {
            HBox container = (HBox) toggle.getUserData();
            container.setDisable(!enabled);
            container.setOpacity(enabled ? 1.0 : 0.5);
        });

        orientationGroup.getToggles().forEach(toggle -> {
            HBox container = (HBox) toggle.getUserData();
            container.setDisable(!enabled);
            container.setOpacity(enabled ? 1.0 : 0.5);
        });
    }

    // Getters
    public Text getQuestionText() { return questionText; }
    public Text getHintText() { return hintText; }
    public Text getAnswerText() { return answerText; }
    public TextField getUserInputField() { return userInputField; }
    public Button getSubmitButton() { return submitButton; }
    public Button getHintButton() { return hintButton; }
    public Button getAnswerButton() { return answerButton; }
    public Button getNewQuestionButton() { return newQuestionButton; }
    public ToggleGroup getModeGroup() { return modeGroup; }
    public ToggleGroup getSizeGroup() { return sizeGroup; }
    public ToggleGroup getOrientationGroup() { return orientationGroup; }
}