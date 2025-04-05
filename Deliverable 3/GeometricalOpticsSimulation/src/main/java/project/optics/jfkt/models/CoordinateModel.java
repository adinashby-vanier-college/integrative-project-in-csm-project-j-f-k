package project.optics.jfkt.models;

public class CoordinateModel {
    double X;
    double Y;

    public CoordinateModel(double X, double Y){
        this.X= X;
        this.Y= Y;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }
}
