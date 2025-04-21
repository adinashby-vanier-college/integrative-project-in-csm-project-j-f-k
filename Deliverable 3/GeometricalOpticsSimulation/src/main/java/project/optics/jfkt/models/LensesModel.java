package project.optics.jfkt.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LensesModel {
    // Primary optical parameters
    private int numRays;
    private double objectDistance;
    private double objectHeight;
    private double magnification;
    private double focalLength;

    // Lens storage using recommended object approach
    private final List<Lens> extraLenses = new ArrayList<>();

    public LensesModel(int numRays, double objectDistance,
                       double objectHeight, double magnification,
                       double focalLength) {
        setNumRays(numRays);
        setObjectDistance(objectDistance);
        setObjectHeight(objectHeight);
        setMagnification(magnification);
        setFocalLength(focalLength);
    }

    // ========================
    // Main Parameter Accessors
    // ========================
    public int getNumRays() { return numRays; }
    public void setNumRays(int numRays) {
        this.numRays = Math.max(0, numRays); // At least 1 ray
    }

    public double getObjectDistance() { return objectDistance; }
    public void setObjectDistance(double objectDistance) {
        this.objectDistance = Math.abs(objectDistance); // Always positive
    }

    public double getObjectHeight() { return objectHeight; }
    public void setObjectHeight(double objectHeight) {
        this.objectHeight = Math.abs(objectHeight); // Always positive
    }

    public double getMagnification() { return magnification; }
    public void setMagnification(double magnification) {
        this.magnification = magnification; // Can be positive or negative
    }

    public double getFocalLength() { return focalLength; }
    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength; // Positive for converging, negative for diverging
    }


    // Extra Lenses
    public List<Lens> getExtraLenses() {
        return Collections.unmodifiableList(extraLenses);
    }


    public void addExtraLens(double position, double focalLength) {
        extraLenses.add(new Lens(position, focalLength));
    }

    public void clearExtraLenses() {
        extraLenses.clear();
    }

    // ========================
    // Lens Data Class
    // ========================
    public static class Lens {
        private final double position; // Relative to main lens
        private final double focalLength;

        public Lens(double position, double focalLength) {
            this.position = position;
            this.focalLength = focalLength;
        }

        public double getPosition() { return position; }
        public double getFocalLength() { return focalLength; }
        public boolean isConverging() { return focalLength > 0; }

        @Override
        public String toString() {
            return String.format("Lens[pos=%.1f, f=%.1f, %s]",
                    position, focalLength,
                    isConverging() ? "converging" : "diverging");
        }
    }


}
