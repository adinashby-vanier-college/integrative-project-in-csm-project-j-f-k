package project.optics.jfkt.models;

import java.util.ArrayList;
import java.util.List;

public class LensesModel {
    //the 5 parameters
    private int numRays;
    private double objectDistance;
    private double objectHeight;
    private double magnification;
    private double focalLength;

    //list of lenses
    private List<Double> extraLenses;

    public LensesModel(int numRays, double objectDistance, double objectHeight, double magnification, double focalLength) {
        this.numRays = numRays;
        this.objectDistance = objectDistance;
        this.objectHeight = objectHeight;
        this.magnification = magnification;
        this.focalLength = focalLength;
        this.extraLenses = new ArrayList<>();
    }
    // Getters and setters
    public int getNumRays() { return numRays; }
    public void setNumRays(int numRays) { this.numRays = numRays; }

    public double getObjectDistance() { return objectDistance; }
    public void setObjectDistance(double objectDistance) { this.objectDistance = objectDistance; }

    public double getObjectHeight() { return objectHeight; }
    public void setObjectHeight(double objectHeight) { this.objectHeight = objectHeight; }

    public double getMagnification() { return magnification; }
    public void setMagnification(double magnification) { this.magnification = magnification; }

    public double getFocalLength() { return focalLength; }
    public void setFocalLength(double focalLength) { this.focalLength = focalLength; }

    public List<Double> getExtraLenses() { return extraLenses; }
    public void addExtraLens(double position) { extraLenses.add(position); }
}
