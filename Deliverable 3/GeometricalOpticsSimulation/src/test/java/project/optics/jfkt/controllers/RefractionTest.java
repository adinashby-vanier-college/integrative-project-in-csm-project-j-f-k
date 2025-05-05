package project.optics.jfkt.controllers;

import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.optics.jfkt.models.Refraction;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RefractionController's calculate2 and calculate3 methods,
 * covering various refraction and reflection scenarios.
 */
public class RefractionTest {
    private RefractionController controller;
    private Refraction refraction;

    @BeforeEach
    public void setUp() {
        HBox layer1 = new HBox();
        HBox layer2 = new HBox();
        HBox layer3 = new HBox();
        layer1.setPrefHeight(10);
        layer2.setPrefHeight(20);
        layer3.setPrefHeight(30);

        layer1.resize(500, layer1.getPrefHeight());
        layer2.resize(500, layer2.getPrefHeight());
        layer3.resize(500, layer3.getPrefHeight());


        refraction = new Refraction();
        refraction.setInitialLocation(5);
        refraction.setLayerCount(3);
        refraction.setLayer1(layer1);
        refraction.setLayer2(layer2);
        refraction.setLayer3(layer3);
        // Default refractive indices
        refraction.setN1(1.0);
        refraction.setN2(1.0);
        refraction.setN3(1.0);
        controller = new RefractionController(refraction, null);
    }

    /**
     * calculate2 with zero angle: straight down through two layers
     */
    @Test
    public void testCalculate2_StraightThrough() {
        refraction.setLayerCount(2);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) controller.calculate2(0);
        assertEquals(3, path.size()); // entry, interface, exit
    }

    /**
     * calculate2: small angle refracts into second layer
     */
    @Test
    public void testCalculate2_RefractAtFirstLayer() {
        refraction.setLayerCount(2);
        refraction.setN1(1.0);
        refraction.setN2(1.54);
        refraction.setInitialAngle(30);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) controller.calculate2(0);
        assertEquals(3, path.size());
        Point2D exitPoint = path.getLast();
        Point2D initialPoint = path.getFirst();
        assertTrue(exitPoint.getX() > initialPoint.getX());
    }

    /**
     * calculate2: total internal reflection at first interface
     */
    @Test
    public void testCalculate2_ReflectAtFirstLayer() {
        refraction.setLayerCount(2);
        refraction.setN1(1.5);
        refraction.setN2(1.0);
        refraction.setInitialAngle(50);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) controller.calculate2(0);
        // reflection: expect only entry and reflection point
        assertEquals(3, path.size());
        Point2D p1 = path.get(0);
        assertEquals(refraction.getInitialLocation(), p1.getX(), 1e-6);
    }

// calculate 3 tests

    /**
     * Reflection between layer1 and layer2 only
     */
    @Test
    public void testCalculate3_ReflectBetweenLayer1And2() throws Exception {
        refraction.setLayerCount(3);
        refraction.setN1(1.54);
        refraction.setN2(1.0);
        refraction.setN3(1.0);
        refraction.setInitialAngle(57.5);
        Method m = RefractionController.class.getDeclaredMethod("calculate3");
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) m.invoke(controller);
        // reflection at first interface only
        assertEquals(3, path.size());
    }

    /**
     * Refract at layer12, reflect at layer23, then refract back and exit
     */
    @Test
    public void testCalculate3_RefractReflectRefract() throws Exception {
        refraction.setLayerCount(3);
        refraction.setN1(1.0);
        refraction.setN2(1.5);
        refraction.setN3(1.0);
        Method m = RefractionController.class.getDeclaredMethod("calculate3");
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) m.invoke(controller);
        // expect entry, interface12, interface23, exit
        assertEquals(4, path.size());
    }

    /**
     * Refract at both interfaces and exit
     */
    @Test
    public void testCalculate3_RefractThroughAllLayers() throws Exception {
        refraction.setLayerCount(3);
        refraction.setN1(1.0);
        refraction.setN2(1.2);
        refraction.setN3(1.5);
        Method m = RefractionController.class.getDeclaredMethod("calculate3");
        m.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Point2D> path = (List<Point2D>) m.invoke(controller);
        // full transmission: entry, interface1, interface2, exit
        assertEquals(4, path.size());
    }
}
