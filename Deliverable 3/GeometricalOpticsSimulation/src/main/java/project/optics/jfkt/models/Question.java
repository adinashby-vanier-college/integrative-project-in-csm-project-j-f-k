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

    // ========== EASY QUESTIONS ========== //
    public static final Question CONCAVE_MIRROR_QUESTION = new Question(
            GeneralSetting.getString("question.concaveMirror.hint"),
            GeneralSetting.getString("question.concaveMirror.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.concaveMirror.text"),
            "/images/easy2.png"
    );

    public static final Question REFRACTION_QUESTION = new Question(
            GeneralSetting.getString("question.refraction1.hint"),
            GeneralSetting.getString("question.refraction1.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.refraction1.text"),
            "/images/easy1.png"
    );

    public static final Question REFRACTION2_QUESTION = new Question(
            GeneralSetting.getString("question.refraction2.hint"),
            GeneralSetting.getString("question.refraction2.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.refraction2.text"),
            "/images/easy3.png"
    );

    public static final Question FISH_DEPTH_QUESTION = new Question(
            GeneralSetting.getString("question.fishDepth.hint"),
            GeneralSetting.getString("question.fishDepth.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.fishDepth.text"),
            "/images/easy4.png"
    );

    public static final Question MIRROR_TYPE_QUESTION = new Question(
            GeneralSetting.getString("question.mirrorType.hint"),
            GeneralSetting.getString("question.mirrorType.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.mirrorType.text"),
            "/images/easy5.png"
    );

    public static final Question CONVEX_MIRROR_QUESTION = new Question(
            GeneralSetting.getString("question.convexMirror.hint"),
            GeneralSetting.getString("question.convexMirror.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.convexMirror.text"),
            "/images/easy6.png"
    );

    public static final Question LIGHT_SPEED_QUESTION = new Question(
            GeneralSetting.getString("question.lightSpeed.hint"),
            GeneralSetting.getString("question.lightSpeed.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.lightSpeed.text"),
            "/images/easy7.png"
    );

    public static final Question PLANE_MIRROR_QUESTION = new Question(
            GeneralSetting.getString("question.planeMirror.hint"),
            GeneralSetting.getString("question.planeMirror.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.planeMirror.text"),
            "/images/easy8.png"
    );

    public static final Question DIAMOND_QUESTION = new Question(
            GeneralSetting.getString("question.diamond.hint"),
            GeneralSetting.getString("question.diamond.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.diamond.text"),
            "/images/easy9.png"
    );

    public static final Question CONVERGING_LENS_QUESTION = new Question(
            GeneralSetting.getString("question.convergingLens.hint"),
            GeneralSetting.getString("question.convergingLens.answer"),
            Difficulty.EASY,
            GeneralSetting.getString("question.convergingLens.text"),
            "/images/easy10.png"
    );

    // ========== MEDIUM QUESTIONS ========== //
    public static final Question CRITICAL_ANGLE_QUESTION = new Question(
            GeneralSetting.getString("question.criticalAngle.hint"),
            GeneralSetting.getString("question.criticalAngle.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.criticalAngle.text"),
            "/images/medium1.png"
    );

    public static final Question TELESCOPE_MAGNIFICATION_QUESTION = new Question(
            GeneralSetting.getString("question.telescopeMagnification.hint"),
            GeneralSetting.getString("question.telescopeMagnification.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.telescopeMagnification.text"),
            "/images/medium2.png"
    );

    public static final Question SIMPLE_MAGNIFIER_QUESTION = new Question(
            GeneralSetting.getString("question.simpleMagnifier.hint"),
            GeneralSetting.getString("question.simpleMagnifier.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.simpleMagnifier.text"),
            "/images/medium3.png"
    );

    public static final Question MICROSCOPE_MAGNIFICATION_QUESTION = new Question(
            GeneralSetting.getString("question.microscopeMagnification.hint"),
            GeneralSetting.getString("question.microscopeMagnification.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.microscopeMagnification.text"),
            "/images/medium4.png"
    );

    public static final Question TELESCOPE_FOCAL_LENGTH_QUESTION = new Question(
            GeneralSetting.getString("question.telescopeFocalLength.hint"),
            GeneralSetting.getString("question.telescopeFocalLength.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.telescopeFocalLength.text"),
            "/images/medium5.png"
    );

    public static final Question CONVEX_LENS_IMAGE_QUESTION = new Question(
            GeneralSetting.getString("question.convexLensImage.hint"),
            GeneralSetting.getString("question.convexLensImage.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.convexLensImage.text"),
            "/images/medium6.png"
    );

    public static final Question MAGNIFIER_POSITION_QUESTION = new Question(
            GeneralSetting.getString("question.magnifierPosition.hint"),
            GeneralSetting.getString("question.magnifierPosition.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.magnifierPosition.text"),
            "/images/medium7.png"
    );

    public static final Question SIMPLE_MAGNIFIER_IMAGE_QUESTION = new Question(
            GeneralSetting.getString("question.simpleMagnifierImage.hint"),
            GeneralSetting.getString("question.simpleMagnifierImage.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.simpleMagnifierImage.text"),
            "/images/medium8.png"
    );

    public static final Question MAGNIFIER_FOCAL_LENGTH_QUESTION = new Question(
            GeneralSetting.getString("question.magnifierFocalLength.hint"),
            GeneralSetting.getString("question.magnifierFocalLength.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.magnifierFocalLength.text"),
            "/images/medium9.png"
    );

    public static final Question LENS_MAGNIFICATION_QUESTION = new Question(
            GeneralSetting.getString("question.lensMagnification.hint"),
            GeneralSetting.getString("question.lensMagnification.answer"),
            Difficulty.MEDIUM,
            GeneralSetting.getString("question.lensMagnification.text"),
            "/images/medium10.png"
    );

    // ========== HARD QUESTIONS ========== //
    public static final Question TWO_LENS_SYSTEM_QUESTION = new Question(
            GeneralSetting.getString("question.twoLensSystem.hint"),
            GeneralSetting.getString("question.twoLensSystem.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.twoLensSystem.text"),
            "/images/hard1.png"
    );

    public static final Question DIVERGING_CONVERGING_LENS_QUESTION = new Question(
            GeneralSetting.getString("question.divergingConvergingLens.hint"),
            GeneralSetting.getString("question.divergingConvergingLens.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.divergingConvergingLens.text"),
            "/images/hard2.png"
    );

    public static final Question MICROSCOPE_IMAGE_LOCATION_QUESTION = new Question(
            GeneralSetting.getString("question.microscopeImageLocation.hint"),
            GeneralSetting.getString("question.microscopeImageLocation.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.microscopeImageLocation.text"),
            "/images/hard3.png"
    );

    public static final Question MULTI_MEDIUM_REFRACTION_QUESTION = new Question(
            GeneralSetting.getString("question.multiMediumRefraction.hint"),
            GeneralSetting.getString("question.multiMediumRefraction.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.multiMediumRefraction.text"),
            "/images/hard4.png"
    );

    public static final Question LASER_BEAM_REFRACTION_QUESTION = new Question(
            GeneralSetting.getString("question.laserBeamRefraction.hint"),
            GeneralSetting.getString("question.laserBeamRefraction.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.laserBeamRefraction.text"),
            "/images/hard5.png"
    );

    public static final Question LENS_MIRROR_SYSTEM_QUESTION = new Question(
            GeneralSetting.getString("question.lensMirrorSystem.hint"),
            GeneralSetting.getString("question.lensMirrorSystem.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.lensMirrorSystem.text"),
            "/images/hard6.png"
    );

    public static final Question TELESCOPE_LENS_SEPARATION_QUESTION = new Question(
            GeneralSetting.getString("question.telescopeLensSeparation.hint"),
            GeneralSetting.getString("question.telescopeLensSeparation.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.telescopeLensSeparation.text"),
            "/images/medium2.png"
    );

    public static final Question CONCAVE_MIRROR_LENS_SYSTEM_QUESTION = new Question(
            GeneralSetting.getString("question.concaveMirrorLensSystem.hint"),
            GeneralSetting.getString("question.concaveMirrorLensSystem.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.concaveMirrorLensSystem.text"),
            "/images/hard8.png"
    );

    public static final Question DIVERGING_LENS_MIRROR_SYSTEM_QUESTION = new Question(
            GeneralSetting.getString("question.divergingLensMirrorSystem.hint"),
            GeneralSetting.getString("question.divergingLensMirrorSystem.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.divergingLensMirrorSystem.text"),
            "/images/hard9.png"
    );

    public static final Question CONCAVE_MIRROR_LENS_SYSTEM2_QUESTION = new Question(
            GeneralSetting.getString("question.concaveMirrorLensSystem2.hint"),
            GeneralSetting.getString("question.concaveMirrorLensSystem2.answer"),
            Difficulty.HARD,
            GeneralSetting.getString("question.concaveMirrorLensSystem2.text"),
            "/images/hard10.png"
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

        // Hard questions
        samples.add(TWO_LENS_SYSTEM_QUESTION);
        samples.add(DIVERGING_CONVERGING_LENS_QUESTION);
        samples.add(MICROSCOPE_IMAGE_LOCATION_QUESTION);
        samples.add(MULTI_MEDIUM_REFRACTION_QUESTION);
        samples.add(LASER_BEAM_REFRACTION_QUESTION);
        samples.add(LENS_MIRROR_SYSTEM_QUESTION);
        samples.add(TELESCOPE_LENS_SEPARATION_QUESTION);
        samples.add(CONCAVE_MIRROR_LENS_SYSTEM_QUESTION);
        samples.add(DIVERGING_LENS_MIRROR_SYSTEM_QUESTION);
        samples.add(CONCAVE_MIRROR_LENS_SYSTEM2_QUESTION);

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