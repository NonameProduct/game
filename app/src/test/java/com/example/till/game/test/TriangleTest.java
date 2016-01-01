package com.example.till.game.test;

import com.example.till.game.Triangle;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 30.12.15.
 */
public class TriangleTest extends GameTestCase {
    public void testEquals() {

        Triangle triangle = new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{30/200.0, 20/200.0}, 4/200.0, 65/200.0);
        assertTrue(triangle.equals(triangle));

        Triangle triangle2;
        triangle2 = new Triangle(new double[]{1/200.0, 1200/200.0}, new double[]{30/200.0, 20/200.0}, 4, 65);
        assertFalse(triangle.equals(triangle2));
        triangle2 = new Triangle(new double[]{400/200.0, 4/200.0}, new double[]{30/200.0, 20/200.0}, 4, 65);
        assertFalse(triangle.equals(triangle2));
        triangle2 = new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{5/200.0, 20/200.0}, 4, 65);
        assertFalse(triangle.equals(triangle2));
        triangle2 = new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{30/200.0, 7/200.0}, 4, 65);
        assertFalse(triangle.equals(triangle2));
        triangle2 = new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{30/200.0, 20/200.0}, 7, 65);
        assertFalse(triangle.equals(triangle2));
        triangle2 = new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{30/200.0, 20/200.0}, 4, 8);
        assertFalse(triangle.equals(triangle2));
    }

    public void testIsInside() {
        Triangle t = new Triangle(new double[]{0, 0}, new double[]{0, 0}, 0, 0);
        assertTrue(t.isInside(new double[]{0, 0}));
        assertFalse(t.isInside(new double[]{0, 300/200.0}));
        assertFalse(t.isInside(new double[]{-200/200.0, -100/200.0}));
        assertFalse(t.isInside(new double[]{200/200.0, -100/200.0}));
    }

    public void testTrianglesCollide() {
        Triangle t = new Triangle(new double[]{0, 0}, new double[]{0, 0}, 0, 0);
        Triangle t180DegreesRotated = new Triangle(new double[]{0, 0}, new double[]{0, 0}, Math.PI, 0);
        Triangle tPiercing = new Triangle(new double[]{74.58640225219403/200.0, -73.259914341275/200.0}, new double[]{0, 0}, 2.670353755551327, 0);
        Triangle somewhereElse = new Triangle(new double[]{0, 300/200.0}, new double[]{0, 0}, 0, 0);
        double[] identityTransformation = makeLinearTransformation(new double[]{0, 0}, new double[]{1, 0, 0, 1});
        assertTrue(t.dockablesCollide(identityTransformation, identityTransformation, t180DegreesRotated));
        assertTrue(t180DegreesRotated.dockablesCollide(identityTransformation, identityTransformation, t));
        assertTrue(t.dockablesCollide(identityTransformation, identityTransformation, tPiercing));
        assertTrue(tPiercing.dockablesCollide(identityTransformation, identityTransformation, t));
        assertFalse(t.dockablesCollide(identityTransformation, identityTransformation, somewhereElse));
        assertFalse(somewhereElse.dockablesCollide(identityTransformation, identityTransformation, t));
    }
}
