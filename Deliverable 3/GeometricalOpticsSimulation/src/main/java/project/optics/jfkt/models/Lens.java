package project.optics.jfkt.models;

public class Lens {
    private double position; // Position relative to main lens
    private double focalLength;
    private boolean isConverging;

    public Lens(double position, double focalLength) {
        this.position = position;
        this.focalLength = focalLength;
        this.isConverging = focalLength > 0;
    }

    // Getters and setters
    public double getPosition() { return position; }
    public double getFocalLength() { return focalLength; }
    public boolean isConverging() { return isConverging; }
}
