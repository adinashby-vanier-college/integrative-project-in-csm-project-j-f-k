package project.optics.jfkt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LensMathTest {
    @Test
    void testCalculateImageDistance() {
        double u = 100;
        double f = 50;
        double expectedV = 100.0; // (1/v = 1/f - 1/u) → v = 100
        double actualV = LensMath.calculateImageDistance(u, f);
        assertEquals(expectedV, actualV, 0.001);
    }

    @Test
    void testCalculateMagnification() {
        double v = 100;
        double u = 50;
        double expectedM = -2.0; // m = -v/u
        double actualM = LensMath.calculateMagnification(v, u);
        assertEquals(expectedM, actualM, 0.001);
    }

    @Test
    void testCalculateImageX() {
        double lensX = 300;
        double v = 100;
        double expectedX = 400;
        double actualX = LensMath.calculateImageX(lensX, v);
        assertEquals(expectedX, actualX, 0.001);
    }

    @Test
    void testCalculateImageY() {
        double lensY = 250;
        double objTopY = 200;
        double m = 2.0;
        double expectedY = 250 - ((250 - 200) * 2); // 250 - 50*2 = 150
        double actualY = LensMath.calculateImageY(lensY, objTopY, m);
        assertEquals(expectedY, actualY, 0.001);
    }

    @Test
    void testObjectAtFocalPoint() {
        double u = 50.0;
        double f = 50.0;
        double v = LensMath.calculateImageDistance(u, f);
        assertEquals(Double.POSITIVE_INFINITY, v);
    }

    @Test
    void testZeroObjectDistanceReturnsInfinity() {
        double u = 0;
        double f = 50.0;
        double result = LensMath.calculateImageDistance(u, f);
        assertTrue(Double.isInfinite(result));
    }



}