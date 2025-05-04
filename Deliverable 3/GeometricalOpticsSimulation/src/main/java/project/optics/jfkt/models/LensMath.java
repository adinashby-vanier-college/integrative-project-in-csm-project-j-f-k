package project.optics.jfkt.models;

public class LensMath {
    public static double calculateImageDistance(double u, double f) {
        return 1.0 / ((1.0 / f) - (1.0 / u));
    }

    public static double calculateMagnification(double v, double u) {
        return -v / u;
    }

    public static double calculateImageX(double lensX, double v) {
        return lensX + v;
    }

    public static double calculateImageY(double lensY, double objTopY, double magnification) {
        return lensY - magnification * (lensY - objTopY);
    }

    public static boolean isVirtual(double u, double f) {
        return (f > 0 && u < f) || f < 0;
    }
}

