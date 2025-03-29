package project.optics.jfkt.controllers;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.views.EducationModeView;

import java.util.*;

public class EducationModeController {
    private EducationModeView view;
    private List<Question> questionBank;
    private List<Question> availableQuestions;
    private Question currentQuestion;
    private Random random = new Random();

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

    private void setupButtonActions() {
        view.getSubmitButton().setOnAction(e -> checkAnswer());
        view.getHintButton().setOnAction(e -> showHint());
        view.getAnswerButton().setOnAction(e -> showAnswer());
        view.getNewQuestionButton().setOnAction(e -> loadNewQuestion());
    }

    private void loadNewQuestion() {
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

        boolean isMirrorQuestion = currentQuestion.isMirrorQuestion();
        boolean isButtonOnlyQuestion = currentQuestion.isButtonOnlyQuestion();

        view.setRadioButtonsEnabled(isMirrorQuestion || isButtonOnlyQuestion);
        view.setUserInputDisabled(isButtonOnlyQuestion);
        view.getUserInputField().setPromptText(isButtonOnlyQuestion ? "Select options below" : "Enter distance answer");

        // Clear previous selections
        view.getModeGroup().getToggles().forEach(t -> t.setSelected(false));
        view.getSizeGroup().getToggles().forEach(t -> t.setSelected(false));
        view.getOrientationGroup().getToggles().forEach(t -> t.setSelected(false));
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
            view.getAnswerText().setText("Error checking answer. Please try again.");
            view.getAnswerText().setFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void checkButtonOnlyQuestionAnswer() {
        try {
            String[] correctParts = currentQuestion.getAnswer().split(",");

            if (correctParts.length < 3) {
                view.getAnswerText().setText("Invalid question configuration");
                view.getAnswerText().setFill(Color.RED);
                return;
            }

            String correctMode = correctParts[0].trim();
            String correctSize = correctParts[1].trim();
            String correctOrientation = correctParts[2].trim();

            String selectedMode = getSelectedMode();
            String selectedSize = getSelectedSize();
            String selectedOrientation = getSelectedOrientation();

            StringBuilder feedback = new StringBuilder();

            // Check mode
            boolean modeCorrect = correctMode.equalsIgnoreCase(selectedMode);
            if (!modeCorrect) {
                feedback.append("Incorrect image type. ");
            }

            // Check size
            boolean sizeCorrect = correctSize.equalsIgnoreCase(selectedSize);
            if (!sizeCorrect) {
                feedback.append("Incorrect size. ");
            }

            // Check orientation
            boolean orientationCorrect = correctOrientation.equalsIgnoreCase(selectedOrientation);
            if (!orientationCorrect) {
                feedback.append("Incorrect orientation. ");
            }

            // Final validation
            if (modeCorrect && sizeCorrect && orientationCorrect) {
                view.getAnswerText().setText("Correct! " + currentQuestion.getAnswer());
                view.getAnswerText().setFill(Color.GREEN);
            } else {
                view.getAnswerText().setText(feedback.toString().trim());
                view.getAnswerText().setFill(Color.RED);
            }
        } catch (Exception e) {
            view.getAnswerText().setText("Error checking answer. Please try again.");
            view.getAnswerText().setFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void checkMirrorQuestionAnswer() {
        try {
            String[] correctParts = currentQuestion.getAnswer().split(",");

            if (correctParts.length < 4) {
                view.getAnswerText().setText("Invalid question configuration");
                view.getAnswerText().setFill(Color.RED);
                return;
            }

            String correctDistance = correctParts[0].trim();
            String correctMode = correctParts[1].trim();
            String correctSize = correctParts[2].trim();
            String correctOrientation = correctParts[3].trim();

            String userAnswer = view.getUserInputField().getText().trim();
            boolean distanceCorrect = userAnswer.equalsIgnoreCase(correctDistance);

            String selectedMode = getSelectedMode();
            String selectedSize = getSelectedSize();
            String selectedOrientation = getSelectedOrientation();

            StringBuilder feedback = new StringBuilder();

            // Check mode
            boolean modeCorrect = correctMode.equalsIgnoreCase(selectedMode);
            if (!modeCorrect) {
                feedback.append("Incorrect image type. ");
            }

            // Check size
            boolean sizeCorrect = correctSize.equalsIgnoreCase("any")
                    ? !selectedSize.isEmpty()
                    : correctSize.equalsIgnoreCase(selectedSize);
            if (!sizeCorrect) {
                if (correctSize.equals("any")) {
                    feedback.append("Please select an image size. ");
                } else {
                    feedback.append("Incorrect size. ");
                }
            }

            // Check orientation
            boolean orientationCorrect = correctOrientation.equalsIgnoreCase(selectedOrientation);
            if (!orientationCorrect) {
                feedback.append("Incorrect orientation. ");
            }

            // Final validation
            if (distanceCorrect && modeCorrect && sizeCorrect && orientationCorrect) {
                view.getAnswerText().setText("Correct! " + currentQuestion.getAnswer());
                view.getAnswerText().setFill(Color.GREEN);
            } else {
                if (!distanceCorrect) {
                    feedback.insert(0, "Incorrect distance. ");
                }
                view.getAnswerText().setText(feedback.toString().trim());
                view.getAnswerText().setFill(Color.RED);
            }
        } catch (Exception e) {
            view.getAnswerText().setText("Error checking answer. Please try again.");
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
            view.getAnswerText().setText("Correct! " + currentQuestion.getAnswer());
            view.getAnswerText().setFill(Color.GREEN);
        } else {
            view.getAnswerText().setText("Incorrect. Try again or click 'Answer'.");
            view.getAnswerText().setFill(Color.RED);
        }
    }

    private void showHint() {
        view.getHintText().setText(currentQuestion.getHint());
    }

    private void showAnswer() {
        view.getAnswerText().setText("Answer: " + currentQuestion.getAnswer());
        view.getAnswerText().setFill(Color.BLUE);
    }
}