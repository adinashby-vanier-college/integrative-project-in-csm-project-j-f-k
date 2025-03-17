package project.optics.jfkt.models;

import project.optics.jfkt.enums.Difficulty;

import java.util.Objects;

public class Question {
    private String hint;
    private String answer;
    private static int nextId = 1;
    private Difficulty difficulty;
    private String description;
    private String image;
    private int id;

    public Question(String hint, String answer, Difficulty difficulty, String description, String image) {
        this.hint = hint;
        this.answer = answer;
        this.difficulty = difficulty;
        this.description = description;
        this.image = image;
        id = nextId++;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id && Objects.equals(hint, question.hint) && Objects.equals(answer, question.answer) && difficulty == question.difficulty && Objects.equals(description, question.description) && Objects.equals(image, question.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hint, answer, difficulty, description, image, id);
    }

    @Override
    public String toString() {
        return "Question{" +
                "hint='" + hint + '\'' +
                ", answer='" + answer + '\'' +
                ", difficulty=" + difficulty +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                '}';
    }
}
