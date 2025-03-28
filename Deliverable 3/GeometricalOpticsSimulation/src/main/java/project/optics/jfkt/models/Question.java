package project.optics.jfkt.models;

import project.optics.jfkt.enums.Difficulty;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private String hint;
    private String answer;
    private static int nextId = 1;
    private Difficulty difficulty;
    private String description;
    private String image;
    private int id;

    public Question(String hint, String answer, Difficulty difficulty,
                    String description, String image) {
        this.hint = hint;
        this.answer = answer;
        this.difficulty = difficulty;
        this.description = description;
        this.image = image;
        this.id = nextId++;
    }

    public static final Question CONCAVE_MIRROR_QUESTION = new Question(
            "Use the mirror equation: 1/f = 1/dₒ + 1/dᵢ",
            "20 cm,real,any,inverted",  // Format: distance,mode,size,orientation
            Difficulty.EASY,
            "A concave mirror has a focal length of 10 cm. If an object is placed 20 cm in front of it, where is the image formed?",
            null
    );

    public static final Question REFRACTION_QUESTION = new Question(
            "Use Snell's Law: n₁sinθ₁ = n₂sinθ₂",
            "48.6",
            Difficulty.EASY,
            "A light ray traveling in glass (n₁ = 1.5) enters the air (n₂ = 1.0) at an angle of 30° from the normal. What is the angle of refraction?",
            null
    );

    public static final Question REFRACTION2_QUESTION = new Question(
            "Use Snell's Law: n₁sinθ₁ = n₂sinθ₂",
            "32.1",
            Difficulty.EASY,
            "Light travels from air  (n₁ = 1.0) into water (n₂ = 1.33) at an angle of incidence of 45°. What is the angle of refraction?",
            null
    );

    public static final Question FISH_DEPTH_QUESTION = new Question(
            "Use the formula: d = n × d' where d is real depth and d' is apparent depth",
            "3.99 m",
            Difficulty.EASY,
            "A fish appears closer to the surface than it really is due to refraction. If the refractive index of water is n = 1.33 and the apparent depth is d' = 3m, find the real depth (d).",
            null
    );

    public static final Question MIRROR_TYPE_QUESTION = new Question(
            "Only one type of mirror always produces virtual, upright, and reduced images",
            "Convex mirror",
            Difficulty.EASY,
            "A mirror forms a virtual, upright, and reduced image. What type of mirror is it?",
            null
    );

    public static final Question CONVEX_MIRROR_QUESTION = new Question(
            "Use the mirror equation: 1/f = 1/dₒ + 1/dᵢ (remember f is negative for convex mirrors)",
            "-10 cm,virtual,smaller,upright",  // Format: distance,mode,size,orientation
            Difficulty.EASY,
            "A convex mirror has a focal length of -15 cm. An object is placed 30 cm in front of it. Find the image distance.",
            null
    );

    public static final Question LIGHT_SPEED_QUESTION = new Question(
            "The speed of light inside a medium is given by v = c/n where c = 3×10⁸ m/s",
            "2x10^8 m/s",
            Difficulty.EASY,
            "A light ray enters a glass prism (n = 1.5) at an angle of 40°. What is the speed of light inside the prism?",
            null
    );

    public static final Question PLANE_MIRROR_QUESTION = new Question(
            "The image distance in a plane mirror is always equal to the object distance.",
            "3 m",
            Difficulty.EASY,
            "A person stands 1.5 meters in front of a plane mirror. How far is their image from them?",
            null
    );

    public static final Question DIAMOND_QUESTION = new Question(
            "The speed of light inside a medium is given by v=c/n",
            "1.24x10^8 m/s",
            Difficulty.EASY,
            "The refractive index of diamond is 2.42. What is the speed of light inside the diamond? (Speed of light in vacuum: 3×10⁸ m/s)",
            null
    );

    public static final Question CONVERGING_LENS_QUESTION = new Question(
            "Use the thin lens equation: 1/f = 1/dₒ + 1/dᵢ and the magnification equation: -dᵢ/dₒ",
            "-1,real,any,inverted",  // Format: distance,mode,size,orientation
            Difficulty.EASY,
            " A converging lens has a focal length of 15 cm. An object is placed 30 cm away. What are the image characteristics (real/virtual, upright/inverted) and find the magnification?",
            null
    );

    public static List<Question> getSampleQuestions() {
        List<Question> samples = new ArrayList<>();
        samples.add(CONCAVE_MIRROR_QUESTION);
        samples.add(REFRACTION_QUESTION);
        samples.add(FISH_DEPTH_QUESTION);
        samples.add(MIRROR_TYPE_QUESTION);
        samples.add(CONVEX_MIRROR_QUESTION);
        samples.add(LIGHT_SPEED_QUESTION);
        samples.add(PLANE_MIRROR_QUESTION);
        samples.add(DIAMOND_QUESTION);
        samples.add(CONVERGING_LENS_QUESTION);
        samples.add(REFRACTION2_QUESTION);
        return samples;
    }

    public boolean isMirrorQuestion() {
        return this == CONCAVE_MIRROR_QUESTION ||
                this == CONVEX_MIRROR_QUESTION ||
                this == CONVERGING_LENS_QUESTION;  // Added to button-based questions
    }

    // Getters and setters
    public String getHint() { return hint; }
    public String getAnswer() { return answer; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public int getId() { return id; }

    public void setHint(String hint) { this.hint = hint; }
    public void setAnswer(String answer) { this.answer = answer; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public void setDescription(String description) { this.description = description; }
    public void setImage(String image) { this.image = image; }
    public void setId(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id &&
                Objects.equals(hint, question.hint) &&
                Objects.equals(answer, question.answer) &&
                difficulty == question.difficulty &&
                Objects.equals(description, question.description) &&
                Objects.equals(image, question.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hint, answer, difficulty, description, image, id);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", difficulty=" + difficulty +
                ", hint='" + hint + '\'' +
                ", answer='" + answer + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}