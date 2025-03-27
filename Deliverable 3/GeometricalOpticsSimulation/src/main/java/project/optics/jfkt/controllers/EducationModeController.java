package project.optics.jfkt.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import project.optics.jfkt.enums.Difficulty;
import project.optics.jfkt.models.Question;
import project.optics.jfkt.views.EducationModeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EducationModeController {
    private EducationModeView view;
    private List<Question> questionBank;
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
        // Filter by difficulty if needed
        questionBank.removeIf(q -> q.getDifficulty() != difficulty);
    }

    private void setupButtonActions() {
        view.getSubmitButton().setOnAction(e -> checkAnswer());
        view.getHintButton().setOnAction(e -> showHint());
        view.getAnswerButton().setOnAction(e -> showAnswer());
        view.getNewQuestionButton().setOnAction(e -> loadNewQuestion());
    }

    private void loadNewQuestion() {
        if (questionBank.isEmpty()) {
            view.getQuestionText().setText("No questions available for this difficulty");
            return;
        }

        currentQuestion = questionBank.get(random.nextInt(questionBank.size()));
        view.getQuestionText().setText(currentQuestion.getDescription());
        view.getHintText().setText("");
        view.getAnswerText().setText("");
        view.getUserInputField().clear();
        view.getAnswerText().setFill(Color.BLACK);
    }

    private void checkAnswer() {
        String userAnswer = view.getUserInputField().getText().trim();
        String correctAnswer = currentQuestion.getAnswer().split("\\(")[0].trim(); // Remove (real,inverted) part

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            view.getAnswerText().setText("Correct! " + currentQuestion.getAnswer());
            view.getAnswerText().setFill(Color.GREEN);
        } else {
            view.getAnswerText().setText("Incorrect. Try again or click 'Answer' to see the solution.");
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