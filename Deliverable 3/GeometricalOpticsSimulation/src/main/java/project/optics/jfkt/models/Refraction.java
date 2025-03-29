package project.optics.jfkt.models;

import javafx.scene.layout.HBox;

import java.util.Objects;

public class Refraction {
    private HBox layer1;
    private HBox layer2;
    private HBox layer3;
    private double n1;
    private double n2;
    private double n3;
    private double initialAngle;
    private double initialLocation;
    private int layerCount;

    public Refraction(HBox layer1, HBox layer2, HBox layer3, double n1, double n2, double n3, double initialAngle, double initialLocation, int layerCount) {
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.initialAngle = initialAngle;
        this.initialLocation = initialLocation;
        this.layerCount = layerCount;
    }

    public Refraction() {}

    public HBox getLayer1() {
        return layer1;
    }

    public void setLayer1(HBox layer1) {
        this.layer1 = layer1;
    }

    public HBox getLayer2() {
        return layer2;
    }

    public void setLayer2(HBox layer2) {
        this.layer2 = layer2;
    }

    public HBox getLayer3() {
        return layer3;
    }

    public void setLayer3(HBox layer3) {
        this.layer3 = layer3;
    }

    public double getN1() {
        return n1;
    }

    public void setN1(double n1) {
        this.n1 = n1;
    }

    public double getN2() {
        return n2;
    }

    public void setN2(double n2) {
        this.n2 = n2;
    }

    public double getN3() {
        return n3;
    }

    public void setN3(double n3) {
        this.n3 = n3;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    public double getInitialAngle() {
        return initialAngle;
    }

    public void setInitialAngle(double initialAngle) {
        this.initialAngle = initialAngle;
    }

    public double getInitialLocation() {
        return initialLocation;
    }

    public void setInitialLocation(double initialLocation) {
        this.initialLocation = initialLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refraction that = (Refraction) o;
        return Double.compare(n1, that.n1) == 0 && Double.compare(n2, that.n2) == 0 && Double.compare(n3, that.n3) == 0 && Double.compare(initialAngle, that.initialAngle) == 0 && Double.compare(initialLocation, that.initialLocation) == 0 && layerCount == that.layerCount && Objects.equals(layer1, that.layer1) && Objects.equals(layer2, that.layer2) && Objects.equals(layer3, that.layer3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer1, layer2, layer3, n1, n2, n3, initialAngle, initialLocation, layerCount);
    }

    @Override
    public String toString() {
        return "Refraction{" +
                "layer1=" + layer1 +
                ", layer2=" + layer2 +
                ", layer3=" + layer3 +
                ", n1=" + n1 +
                ", n2=" + n2 +
                ", n3=" + n3 +
                ", initialAngle=" + initialAngle +
                ", initialLocation=" + initialLocation +
                ", layerCount=" + layerCount +
                '}';
    }
}
