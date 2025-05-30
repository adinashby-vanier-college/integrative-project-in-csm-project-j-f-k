package project.optics.jfkt.views;

import javafx.application.Platform;
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
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.GeneralSetting;
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
    private Pane imagePane;
    private Button zoomIn;
    private Button zoomOut;
    private Button back;

    // Radio button groups
    private ToggleGroup modeGroup;
    private ToggleGroup sizeGroup;
    private ToggleGroup orientationGroup;

    // Font sizes
    private static final double QUESTION_FONT_SIZE = 16.0;
    private static final double HINT_FONT_SIZE = 16.0;
    private static final double ANSWER_FONT_SIZE = 16.0;
    private static final double INPUT_FONT_SIZE = 14.0;

    public EducationModeView(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.setTop(createTop());
        this.setCenter(createCenter());
        this.setRight(createRight());
        new EducationModeController(this, difficulty);

        // Apply initial font
        updateAllFontStyles();

        // Register for font changes
        ThemeController.addFontChangeListener(font -> {
            Platform.runLater(this::updateAllFontStyles);
        });
    }

    private void updateAllFontStyles() {
        String fontFamily = ThemeController.getCurrentFont();

        // Apply to question text
        questionText.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                fontFamily, QUESTION_FONT_SIZE));

        // Apply to hint text
        hintText.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                fontFamily, HINT_FONT_SIZE));

        // Apply to answer text
        answerText.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                fontFamily, ANSWER_FONT_SIZE));

        // Apply to radio button labels
        updateRadioButtonFonts(modeGroup, fontFamily);
        updateRadioButtonFonts(sizeGroup, fontFamily);
        updateRadioButtonFonts(orientationGroup, fontFamily);

        // Apply to text field
        userInputField.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                fontFamily, INPUT_FONT_SIZE));

        // Apply to buttons
        applyFontToButtons(fontFamily);
    }
    private void updateRadioButtonFonts(ToggleGroup group, String fontFamily) {
        for (Toggle toggle : group.getToggles()) {
            HBox container = (HBox) toggle.getUserData();
            if (container != null) {
                Node firstChild = container.getChildren().get(0);
                if (firstChild instanceof Label) {
                    Label label = (Label) firstChild;
                    label.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                            fontFamily, INPUT_FONT_SIZE));
                }
            }
        }
    }
    private void applyFontToButtons(String fontFamily) {
        String buttonStyle = String.format("-fx-font-family: '%s';", fontFamily);
        submitButton.setStyle(buttonStyle);
        hintButton.setStyle(buttonStyle);
        answerButton.setStyle(buttonStyle);
        newQuestionButton.setStyle(buttonStyle);
        zoomIn.setStyle(buttonStyle);
        zoomOut.setStyle(buttonStyle);
        back.setStyle(buttonStyle);
    }

    private Region createTop() {
        return util.createMenu();
    }

    public HBox createZoomAndBackButtons() {
        HBox container = new HBox(20);

        zoomIn = new Button(GeneralSetting.getString("button.zoomIn"));
        zoomOut = new Button(GeneralSetting.getString("button.zoomOut"));
        back = new Button(GeneralSetting.getString("button.back"));


        container.getChildren().addAll(zoomIn, zoomOut, back);

        return container;
    }


    private Region createCenter() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("education-outline");
        container.setBorder(Border.stroke(Color.BLACK));

        HBox topButtons = createZoomAndBackButtons();
        topButtons.setAlignment(Pos.CENTER_LEFT);
        topButtons.setSpacing(10);
        topButtons.setPrefHeight(50);
        HBox.setMargin(topButtons, new Insets(10, 0, 0, 0));

        // Hint area - Add style class
        StackPane hintArea = new StackPane();
        hintArea.setPrefSize(300, 100);
        hintArea.setBorder(Border.stroke(Color.BLACK));
        hintArea.getStyleClass().add("hint-area");  // Add this line
        hintText = new Text();
        hintText.setFont(new Font(HINT_FONT_SIZE));
        hintText.getStyleClass().add("hint-text");
        hintText.setWrappingWidth(280);
        hintArea.setPadding(new Insets(5));
        hintArea.getChildren().add(hintText);

        // Answer area - Add style class
        StackPane answerArea = new StackPane();
        answerArea.setPrefSize(300, 100);
        answerArea.setBorder(Border.stroke(Color.BLACK));
        answerArea.getStyleClass().add("answer-area");  // Add this line
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

        submitButton = new Button(GeneralSetting.getString("button.submit"));
        hintButton = new Button(GeneralSetting.getString("button.hint"));
        answerButton = new Button(GeneralSetting.getString("button.answer"));

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
        questionPane.getStyleClass().add("education-outline");
        questionPane.setBorder(Border.stroke(Color.BLACK));

        questionText = new Text(GeneralSetting.getString("text.question"));
        questionText.setFont(new Font(QUESTION_FONT_SIZE));
        questionText.getStyleClass().add("question-text");
        questionPane.setTop(questionText);

        // Create a StackPane as the image container to center content
        StackPane imageContainer = new StackPane();
        imageContainer.setMinSize(600, 400);
        imageContainer.setPrefSize(600, 400);
        imageContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        imageContainer.getStyleClass().add("image-container"); // Add this line
        imageContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: rgb(128,128,128);");

        // The actual image pane that will hold the image
        imagePane = new Pane();
        imagePane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        imagePane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        imagePane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        imageContainer.getChildren().add(imagePane);
        questionPane.setCenter(imageContainer);

        return questionPane;
    }

    private Region createRight() {
        VBox container = new VBox(100);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        container.setPrefWidth(250);

        // Radio button groups
        modeGroup = new ToggleGroup();
        Node virtualOption = createCustomRadioOption(GeneralSetting.getString("radioButton.virtual"), modeGroup);
        Node realOption = createCustomRadioOption(GeneralSetting.getString("radioButton.real"), modeGroup);
        VBox modeGroupBox = new VBox(10, virtualOption, realOption);

        sizeGroup = new ToggleGroup();
        Node biggerOption = createCustomRadioOption(GeneralSetting.getString("radioButton.bigger"), sizeGroup);
        Node smallerOption = createCustomRadioOption(GeneralSetting.getString("radioButton.smaller"), sizeGroup);
        VBox sizeGroupBox = new VBox(10, biggerOption, smallerOption);

        orientationGroup = new ToggleGroup();
        Node uprightOption = createCustomRadioOption(GeneralSetting.getString("radioButton.upright"), orientationGroup);
        Node invertedOption = createCustomRadioOption(GeneralSetting.getString("radioButton.inverted"), orientationGroup);
        VBox orientationGroupBox = new VBox(10, uprightOption, invertedOption);

        // User input and new question button
        userInputField = new TextField();
        userInputField.setFont(new Font(INPUT_FONT_SIZE));
        userInputField.setPromptText(GeneralSetting.getString("text.distance"));
        userInputField.setMaxWidth(Double.MAX_VALUE);

        newQuestionButton = new Button(GeneralSetting.getString("button.newQuestion"));
        newQuestionButton.setMaxWidth(Double.MAX_VALUE);

        container.getChildren().addAll(modeGroupBox, sizeGroupBox, orientationGroupBox, userInputField, newQuestionButton);
        return container;
    }

    private Node createCustomRadioOption(String text, ToggleGroup group) {
        Label label = new Label(text);
        label.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %.1f;",
                ThemeController.getCurrentFont(), INPUT_FONT_SIZE));

        RadioButton rb = new RadioButton();
        rb.setToggleGroup(group);
        rb.setStyle("-fx-opacity: 0; -fx-padding: 0; -fx-min-width: 0; -fx-min-height: 0;");

        Rectangle square = new Rectangle(15, 15);
        square.getStyleClass().add("radio-square");
        square.setStroke(Color.BLACK);
        square.setFill(Color.TRANSPARENT);
        rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            square.setFill(isSelected ?
                    ThemeController.getCurrentTheme().equals("dark-mode") ? Color.WHITE : Color.BLACK
                    : Color.TRANSPARENT);
        });

        // Add listener for theme changes
        ThemeController.addThemeChangeListener(theme -> {
            if (theme.equals("dark-mode")) {
                square.setStroke(Color.WHITE);
                if (rb.isSelected()) {
                    square.setFill(Color.WHITE);
                }
            } else {
                square.setStroke(Color.BLACK);
                if (rb.isSelected()) {
                    square.setFill(Color.BLACK);
                }
            }
        });

        HBox optionContainer = new HBox(10);
        optionContainer.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        optionContainer.getChildren().addAll(label, spacer, square, rb);
        rb.setManaged(false);
        rb.setVisible(false);

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

    public void setUserInputDisabled(boolean disabled) {
        userInputField.setDisable(disabled);
        userInputField.setOpacity(disabled ? 0.5 : 1.0);
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
    public Pane getImagePane() {return imagePane;}
    public Button getZoomInButton() {return zoomIn;}
    public Button getZoomOutButton() {return zoomOut;}
    public Button getBackButton(){return back;}

}