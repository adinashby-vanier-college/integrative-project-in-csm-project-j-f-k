package project.optics.jfkt.models;

public class CoordinateModel {
    double X;
    double Y;

    public CoordinateModel(double X, double Y){
        this.X= (double) Math.round(X * 10) /10;
        this.Y= (double) Math.round(Y * 10) /10;
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
