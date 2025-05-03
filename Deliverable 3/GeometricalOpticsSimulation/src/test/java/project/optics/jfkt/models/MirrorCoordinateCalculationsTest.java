package project.optics.jfkt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MirrorCoordinateCalculationsTest {
    private static final double centerX = 750;
    private static final double centerY = 360;
    private static final double scale = 10;
    private static final double objectHeight = 8.0;
    private static final double focalLength = 8.0;
    //set One of coordinate
    CoordinateModel testOneCoord = new CoordinateModel(616.7, 413.3);
    CoordinateModel testTwoCoord = new CoordinateModel(990.0, 40.0);
    CoordinateModel testThreeCoord = new CoordinateModel(807.1, 337.1);
    CoordinateModel testFourCoord = new CoordinateModel(784.3, 314.3);

    //test concave image generation location
    //behind focal point
    @Test
    void behindConcave(){
        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,20.0,objectHeight,true);
        double X = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getX();
        double Y = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getY();
        assertEquals(testOneCoord.getX() , X);
        assertEquals(testOneCoord.getY() , Y);
    }
    //infront focal point
    @Test
    void infrontConcave(){
        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,6.0,objectHeight,true);
        double X = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getX();
        double Y = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getY();
        assertEquals(testTwoCoord.getX() , X);
        assertEquals(testTwoCoord.getY() , Y);
    }
    //Test Convex mage generation location
    //behind focal point
    @Test
    void behindConvex(){
        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,20.0,objectHeight,false);
        double X = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getX();
        double Y = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getY();
        assertEquals(testThreeCoord.getX() , X);
        assertEquals(testThreeCoord.getY() , Y);
    }
    //infront focal point
    @Test
    void infrontConvex(){
        MirrorCoordinateCalculations mirrorCoordinateCalculations = new MirrorCoordinateCalculations(focalLength,scale,centerX,centerY,6.0,objectHeight,false);
        double X = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getX();
        double Y = mirrorCoordinateCalculations.getFirstCoordinateSet().getLast().getY();
        assertEquals(testFourCoord.getX() , X);
        assertEquals(testFourCoord.getY() , Y);
    }
}