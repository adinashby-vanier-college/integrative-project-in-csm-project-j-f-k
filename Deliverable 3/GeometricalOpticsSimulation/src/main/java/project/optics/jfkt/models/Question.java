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

    // Easy questions
    public static final Question CONCAVE_MIRROR_QUESTION = new Question(
            "Use the mirror equation: 1/f = 1/dₒ + 1/dᵢ",
            "20 cm,real,any,inverted",
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
            "Light travels from air (n₁ = 1.0) into water (n₂ = 1.33) at an angle of incidence of 45°. What is the angle of refraction?",
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
            "-10 cm,virtual,smaller,upright",
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
            "-1,real,any,inverted",
            Difficulty.EASY,
            "A converging lens has a focal length of 15 cm. An object is placed 30 cm away. What are the image characteristics (real/virtual, upright/inverted) and find the magnification?",
            null
    );

    // Medium questions
    public static final Question CRITICAL_ANGLE_QUESTION = new Question(
            "Use the critical angle formula: θc = sin⁻¹(n₂/n₁)",
            "62.5",
            Difficulty.MEDIUM,
            "A light ray travels from glass (n = 1.5) to water (n = 1.33). What is the critical angle at which total internal reflection occurs?",
            null
    );

    public static final Question TELESCOPE_MAGNIFICATION_QUESTION = new Question(
            "M = fₒ/fₑ",
            "20",
            Difficulty.MEDIUM,
            "A telescope has an objective lens (f = 100 cm) and eyepiece (f = 5 cm). What is the angular magnification?",
            null
    );

    public static final Question SIMPLE_MAGNIFIER_QUESTION = new Question(
            "M = 1 + D/f where D is near point distance (25 cm)",
            "3.5",
            Difficulty.MEDIUM,
            "A simple magnifier has a focal length of 10 cm. What is its angular magnification for a normal eye?",
            null
    );

    public static final Question MICROSCOPE_MAGNIFICATION_QUESTION = new Question(
            "M = Mₒ × Mₑ = (L/fₒ) × (D/fₑ)",
            "500",
            Difficulty.MEDIUM,
            "A microscope has an objective lens of focal length 4 mm and an eyepiece lens of focal length 20 mm. What is the total magnification if the tube length is 160 mm?",
            null
    );

    public static final Question TELESCOPE_FOCAL_LENGTH_QUESTION = new Question(
            "Telescope magnification depends on focal length",
            "Magnification increases",
            Difficulty.MEDIUM,
            "If the focal length of an objective lens in a telescope is increased, what happens to its magnification?",
            null
    );

    public static final Question CONVEX_LENS_IMAGE_QUESTION = new Question(
            "Use the lens equation: 1/f = 1/dₒ + 1/dᵢ",
            "7.5 cm",
            Difficulty.MEDIUM,
            "A converging lens of focal length 5 cm is used as a magnifier. If the object is placed at 3 cm, find the image distance.",
            null
    );

    public static final Question MAGNIFIER_POSITION_QUESTION = new Question(
            "The object must be placed where the image remains virtual",
            "Inside the focal point",
            Difficulty.MEDIUM,
            "If a converging lens is used as a magnifier, should the object be placed inside or outside the focal point?",
            null
    );

    public static final Question SIMPLE_MAGNIFIER_IMAGE_QUESTION = new Question(
            "The image is always on the same side as the object",
            "virtual,bigger,upright",
            Difficulty.MEDIUM,
            "What kind of image does a simple magnifier produce?",
            null
    );

    // New questions
    public static final Question MAGNIFIER_FOCAL_LENGTH_QUESTION = new Question(
            "Rearrange the formula M = 1 + D/f to solve for f",
            "6.25 cm",
            Difficulty.MEDIUM,
            "A simple magnifier provides a 5× magnification. If the near point is 25 cm, what is the focal length of the lens?",
            null
    );

    public static final Question LENS_MAGNIFICATION_QUESTION = new Question(
            "First find di using the lens equation, then use M = -di/do",
            "2",
            Difficulty.MEDIUM,
            "What is the magnification if an object is placed 5 cm in front of a convex lens with a focal length of 10 cm?",
            null
    );

    public static List<Question> getSampleQuestions() {
        List<Question> samples = new ArrayList<>();
        // Easy questions
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

        // Medium questions
        samples.add(CRITICAL_ANGLE_QUESTION);
        samples.add(TELESCOPE_MAGNIFICATION_QUESTION);
        samples.add(SIMPLE_MAGNIFIER_QUESTION);
        samples.add(MICROSCOPE_MAGNIFICATION_QUESTION);
        samples.add(TELESCOPE_FOCAL_LENGTH_QUESTION);
        samples.add(CONVEX_LENS_IMAGE_QUESTION);
        samples.add(MAGNIFIER_POSITION_QUESTION);
        samples.add(SIMPLE_MAGNIFIER_IMAGE_QUESTION);
        samples.add(MAGNIFIER_FOCAL_LENGTH_QUESTION);
        samples.add(LENS_MAGNIFICATION_QUESTION);

        return samples;
    }

    public boolean isMirrorQuestion() {
        return this == CONCAVE_MIRROR_QUESTION ||
                this == CONVEX_MIRROR_QUESTION ||
                this == CONVERGING_LENS_QUESTION;
    }

    public boolean isButtonOnlyQuestion() {
        return this == SIMPLE_MAGNIFIER_IMAGE_QUESTION;
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