package project.optics.jfkt.controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.views.SelectionView;
import project.optics.jfkt.views.EducationModeView;

import java.io.InputStream;
import java.util.*;

import static project.optics.jfkt.MainApp.primaryStage;

public class EducationModeController {
    private EducationModeView view;
    private List<Question> questionBank;
    private List<Question> availableQuestions;
    private Question currentQuestion;
    private Random random = new Random();
    private ImageView imageView;
    private Image image;

    public EducationModeController(EducationModeView view, Difficulty difficulty) {
        this.view = view;
        initializeQuestionBank(difficulty);
        setupButtonActions();
        loadNewQuestion();
    }


    private void initializeQuestionBank(Difficulty difficulty) {
        questionBank = new ArrayList<>(Question.getSampleQuestions());
        questionBank.removeIf(q -> q.getDifficulty() != difficulty);
        availableQuestions = new ArrayList<>(questionBank);
        Collections.shuffle(availableQuestions);
    }
    private void onBackButtonPressed(){
        SelectionView selectionView = new SelectionView();
        Scene selectionScene = new Scene(selectionView);
        ThemeController.applyTheme(selectionScene);
        primaryStage.setScene(selectionScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }

    private void onZoomInButtonPressed(){
    imageView.setFitHeight(image.getHeight()*1.1);
    imageView.setFitWidth(image.getWidth()*1.1);
    }
    private void onZoomOutButtonPressed(){
        imageView.setFitHeight(image.getHeight()*0.9);
        imageView.setFitWidth(image.getWidth()*0.9);

    }

    private void setupButtonActions() {
        view.getSubmitButton().setOnAction(e -> checkAnswer());
        view.getHintButton().setOnAction(e -> showHint());
        view.getAnswerButton().setOnAction(e -> showAnswer());
        view.getNewQuestionButton().setOnAction(e -> loadNewQuestion());
        view.getBackButton().setOnAction(e->onBackButtonPressed());
        view.getZoomInButton().setOnAction(e->onZoomInButtonPressed());
        view.getZoomOutButton().setOnAction(e-> onZoomOutButtonPressed());
    }

    private void loadNewQuestion() {
        if (view.getImagePane() == null) {
            System.err.println("Image pane not initialized!");
            return;
        }
        if (availableQuestions.isEmpty()) {
            if (questionBank.isEmpty()) {
                view.getQuestionText().setText("No questions available for this difficulty");
                return;
            }
            availableQuestions = new ArrayList<>(questionBank);
            Collections.shuffle(availableQuestions);
        }

        if (currentQuestion != null) {
            availableQuestions.remove(currentQuestion);
        }

        currentQuestion = availableQuestions.get(random.nextInt(availableQuestions.size()));

        view.getQuestionText().setText(currentQuestion.getDescription());
        view.getHintText().setText("");
        view.getAnswerText().setText("");
        view.getUserInputField().clear();
        view.getAnswerText().setFill(Color.BLACK);

        loadQuestionImage();

        boolean isMirrorQuestion = currentQuestion.isMirrorQuestion();
        boolean isButtonOnlyQuestion = currentQuestion.isButtonOnlyQuestion();

        view.setRadioButtonsEnabled(isMirrorQuestion || isButtonOnlyQuestion);
        view.setUserInputDisabled(isButtonOnlyQuestion);
        view.getUserInputField().setPromptText(isButtonOnlyQuestion ? GeneralSetting.getString("text.selectOption") : GeneralSetting.getString("text.distance"));

        // Clear previous selections
        view.getModeGroup().getToggles().forEach(t -> t.setSelected(false));
        view.getSizeGroup().getToggles().forEach(t -> t.setSelected(false));
        view.getOrientationGroup().getToggles().forEach(t -> t.setSelected(false));
    }
    private void loadQuestionImage() {
        Pane imagePane = view.getImagePane();
        if (imagePane == null) {
            System.err.println("Image pane is null!");
            return;
        }

        imagePane.getChildren().clear();

        if (currentQuestion.getImage() != null && !currentQuestion.getImage().isEmpty()) {
            try {
                InputStream imageStream = getClass().getResourceAsStream(currentQuestion.getImage());
                if (imageStream != null) {
                     image = new Image(imageStream);
                     imageView = new ImageView(image);

                    // Preserve aspect ratio
                    imageView.setPreserveRatio(true);

                    // Calculate available space using the parent container's bounds
                    Region parentContainer = (Region) imagePane.getParent();
                    double maxWidth = parentContainer.getWidth() - 40;
                    double maxHeight = parentContainer.getHeight() - 40;

                    // Add listener to handle layout changes
                    parentContainer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
                        double containerWidth = newVal.getWidth() - 40;
                        double containerHeight = newVal.getHeight() - 40;

                        // Scale to fit while maintaining aspect ratio
                        if (image.getWidth() > containerWidth || image.getHeight() > containerHeight) {
                            double scaleFactor = Math.min(
                                    containerWidth / image.getWidth(),
                                    containerHeight / image.getHeight()
                            );
                            imageView.setFitWidth(image.getWidth() * scaleFactor);
                            imageView.setFitHeight(image.getHeight() * scaleFactor);
                        } else {
                            // Use original size if smaller than available space
                            imageView.setFitWidth(image.getWidth());
                            imageView.setFitHeight(image.getHeight());
                        }

                        // Center the image in the pane
                        imageView.setTranslateX((containerWidth - imageView.getFitWidth()) / 2);
                        imageView.setTranslateY((containerHeight - imageView.getFitHeight()) / 2);
                    });

                    // Trigger initial layout
                    parentContainer.requestLayout();

                    imagePane.getChildren().add(imageView);
                } else {
                    System.err.println("Image not found: " + currentQuestion.getImage());
                    showPlaceholder(imagePane);
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                showPlaceholder(imagePane);
            }
        } else {
            showPlaceholder(imagePane);
        }
    }

    private void showPlaceholder(Pane imagePane) {
        Region parentContainer = (Region) imagePane.getParent();
        double width = parentContainer.getWidth() - 40;
        double height = parentContainer.getHeight() - 40;

        Rectangle placeholder = new Rectangle(width, height, Color.LIGHTGRAY);
        placeholder.setStroke(Color.DARKGRAY);
        placeholder.setArcWidth(10);
        placeholder.setArcHeight(10);

        // Add listener to handle layout changes
        parentContainer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double containerWidth = newVal.getWidth() - 40;
            double containerHeight = newVal.getHeight() - 40;

            placeholder.setWidth(containerWidth);
            placeholder.setHeight(containerHeight);
        });

        // Trigger initial layout
        parentContainer.requestLayout();

        imagePane.getChildren().addAll(placeholder);
    }

    private void checkAnswer() {
        try {
            if (currentQuestion.isButtonOnlyQuestion()) {
                checkButtonOnlyQuestionAnswer();
            } else if (currentQuestion.isMirrorQuestion()) {
                checkMirrorQuestionAnswer();
            } else {
                checkStandardQuestionAnswer();
            }
        } catch (Exception e) {
            view.getAnswerText().setText(GeneralSetting.getString("text.errorChecking"));
            view.getAnswerText().setFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void checkButtonOnlyQuestionAnswer() {
        try {
            String[] correctParts = currentQuestion.getAnswer().split(",");

            if (correctParts.length < 3) {
                view.getAnswerText().setText(
                        GeneralSetting.getString("text.invalidConfig")
                );
                view.getAnswerText().setFill(Color.RED);
                return;
            }

            String correctMode        = correctParts[0].trim();
            String correctSize        = correctParts[1].trim();
            String correctOrientation = correctParts[2].trim();

            String selectedMode        = getSelectedMode();
            String selectedSize        = getSelectedSize();
            String selectedOrientation = getSelectedOrientation();

            StringBuilder feedback = new StringBuilder();

            // Check mode
            boolean modeCorrect = correctMode.equalsIgnoreCase(selectedMode);
            if (!modeCorrect) {
                feedback.append(
                        GeneralSetting.getString("text.incorrectImageType")
                );
            }

            // Check size
            boolean sizeCorrect = correctSize.equalsIgnoreCase(selectedSize);
            if (!sizeCorrect) {
                feedback.append(
                        GeneralSetting.getString("text.incorrectSize")
                );
            }

            // Check orientation
            boolean orientationCorrect = correctOrientation.equalsIgnoreCase(selectedOrientation);
            if (!orientationCorrect) {
                feedback.append(
                        GeneralSetting.getString("text.incorrectOrientation")
                );
            }

            // Final validation
            if (modeCorrect && sizeCorrect && orientationCorrect) {
                view.getAnswerText().setText(
                        GeneralSetting.getString("text.correctAnswer")
                                + currentQuestion.getAnswer()
                );
                view.getAnswerText().setFill(Color.GREEN);
            } else {
                view.getAnswerText().setText(feedback.toString().trim());
                view.getAnswerText().setFill(Color.RED);
            }

        } catch (Exception e) {
            view.getAnswerText().setText(
                    GeneralSetting.getString("text.checkError")
            );
            view.getAnswerText().setFill(Color.RED);
            e.printStackTrace();
        }
    }



    private void checkMirrorQuestionAnswer() {
        try {
            String[] correctParts = currentQuestion.getAnswer().split(",");

            if (correctParts.length < 4) {
                view.getAnswerText().setText(GeneralSetting.getString("text.invalidConfig"));
                view.getAnswerText().setFill(Color.RED);
                return;
            }

            String correctDistance    = correctParts[0].trim();
            String correctMode        = correctParts[1].trim();
            String correctSize        = correctParts[2].trim();
            String correctOrientation = correctParts[3].trim();

            String userAnswer = view.getUserInputField().getText().trim();
            boolean distanceCorrect = userAnswer.equalsIgnoreCase(correctDistance);

            String selectedMode        = getSelectedMode();
            String selectedSize        = getSelectedSize();
            String selectedOrientation = getSelectedOrientation();

            StringBuilder feedback = new StringBuilder();

            // Check mode
            boolean modeCorrect = correctMode.equalsIgnoreCase(selectedMode);
            if (!modeCorrect) {
                feedback.append(GeneralSetting.getString("text.incorrectImageType"));
            }

            // Check size
            boolean sizeCorrect = "any".equalsIgnoreCase(correctSize)
                    ? !selectedSize.isEmpty()
                    : correctSize.equalsIgnoreCase(selectedSize);
            if (!sizeCorrect) {
                if ("any".equalsIgnoreCase(correctSize)) {
                    feedback.append(GeneralSetting.getString("text.selectImageSize"));
                } else {
                    feedback.append(GeneralSetting.getString("text.incorrectSize"));
                }
            }

            // Check orientation
            boolean orientationCorrect = correctOrientation.equalsIgnoreCase(selectedOrientation);
            if (!orientationCorrect) {
                feedback.append(GeneralSetting.getString("text.incorrectOrientation"));
            }

            // Final validation
            if (distanceCorrect && modeCorrect && sizeCorrect && orientationCorrect) {
                view.getAnswerText().setText(
                        GeneralSetting.getString("text.correctAnswer")
                                + currentQuestion.getAnswer()
                );
                view.getAnswerText().setFill(Color.GREEN);
            } else {
                if (!distanceCorrect) {
                    // put distance error at front
                    feedback.insert(0, GeneralSetting.getString("text.incorrectDistance"));
                }
                view.getAnswerText().setText(feedback.toString().trim());
                view.getAnswerText().setFill(Color.RED);
            }

        } catch (Exception e) {
            view.getAnswerText().setText(GeneralSetting.getString("text.checkError"));
            view.getAnswerText().setFill(Color.RED);
            e.printStackTrace();
        }
    }

    private String getSelectedMode() {
        Toggle selected = view.getModeGroup().getSelectedToggle();
        if (selected != null) {
            HBox container = (HBox) selected.getUserData();
            Label label = (Label) container.getChildren().get(0);
            return label.getText();
        }
        return "";
    }

    private String getSelectedSize() {
        Toggle selected = view.getSizeGroup().getSelectedToggle();
        if (selected != null) {
            HBox container = (HBox) selected.getUserData();
            Label label = (Label) container.getChildren().get(0);
            return label.getText();
        }
        return "";
    }

    private String getSelectedOrientation() {
        Toggle selected = view.getOrientationGroup().getSelectedToggle();
        if (selected != null) {
            HBox container = (HBox) selected.getUserData();
            Label label = (Label) container.getChildren().get(0);
            return label.getText();
        }
        return "";
    }

    private void checkStandardQuestionAnswer() {
        String userAnswer = view.getUserInputField().getText().trim();
        String correctAnswer = currentQuestion.getAnswer().split("\\(")[0].trim();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            view.getAnswerText().setText(GeneralSetting.getString("text.correct") + currentQuestion.getAnswer());
            view.getAnswerText().setFill(Color.GREEN);
        } else {
            view.getAnswerText().setText(GeneralSetting.getString("text.incorrect"));
            view.getAnswerText().setFill(Color.RED);
        }
    }

    private void showHint() {
        view.getHintText().setText(currentQuestion.getHint());
    }

    private void showAnswer() {
        view.getAnswerText().setText(GeneralSetting.getString("label.answer") + currentQuestion.getAnswer());
        view.getAnswerText().setFill(Color.BLUE);
    }
}