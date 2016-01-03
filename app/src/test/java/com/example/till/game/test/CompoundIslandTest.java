package com.example.till.game.test;

import com.example.till.game.CompoundIsland;
import com.example.till.game.Triangle;

/**
 * Created by till on 03.01.16.
 */
public class CompoundIslandTest extends GameTestCase {


    public void testIsInsideWorksCorrectly() {
//        given
        CompoundIsland island = aCompoundIsland();

//        then
        pointsJustOutsideAreRecognizedAsOutside(island);
        pointsJustInsideAreRecognizedAsInside(island);
    }

    private void pointsJustInsideAreRecognizedAsInside(CompoundIsland island) {
        double[][] pointsJustOutside = new double[][]{
                new double[]{3+Triangle.HEIGHT*2.0/3.0*0.99, 5},
                new double[]{3-Triangle.HEIGHT*4.0/3.0*0.99, 5},
                new double[]{3-Triangle.HEIGHT*1.0/3.0, 5+0.5*0.99},
                new double[]{3-Triangle.HEIGHT*1.0/3.0, 5-0.5*0.99},
        };
        for (double[] d : pointsJustOutside) {
            assertTrue(island.isInside(d));
        }

    }

    private void pointsJustOutsideAreRecognizedAsOutside(CompoundIsland island) {
        double[][] pointsJustOutside = new double[][]{
                new double[]{3+Triangle.HEIGHT*2.0/3.0*1.01, 5},
                new double[]{3-Triangle.HEIGHT*4.0/3.0*1.01, 5},
                new double[]{3-Triangle.HEIGHT*1.0/3.0, 5+0.5*1.01},
                new double[]{3-Triangle.HEIGHT*1.0/3.0, 5-0.5*1.01},
        };
        for (double[] d : pointsJustOutside) {
            assertFalse(island.isInside(d));
        }
    }

    private CompoundIsland aCompoundIsland() {
        return createCompoundIsland();
    }

    public void testUpdatedDiamondCollidesWithTriangles() {
//        given
        CompoundIsland island = createUpdatedCompoundIsland();

//        when
        Triangle[] triangles = trianglesOnlyJustDoNotCollide();

//        then
        noCollisionsAreDetected(triangles, island);

//        when
        triangles = trianglesOnlyJustCollide();

//        then
        collisionsAreDetected(triangles, island);

    }

    private CompoundIsland createUpdatedCompoundIsland() {
        Triangle t1 = new Triangle(new double[]{1, 4}, new double[]{0, 0}, 4*Math.PI, 0);
        Triangle t2 = new Triangle(new double[]{1, 4+2*Triangle.HEIGHT/3}, new double[]{0, 0}, -11*Math.PI, 0);
        CompoundIsland island = new CompoundIsland(t1, t2);
        island.setMovement(new double[]{2-Triangle.HEIGHT/3, 1-Triangle.HEIGHT/3});
        island.setRotationSpeed(Math.PI / 2);
        island.update();
        return island;
    }

    public void testUnupdatedDiamondCollidesWithTriangles(){
//        given
        CompoundIsland island = createCompoundIsland();

//        when
        Triangle[] triangles = trianglesOnlyJustDoNotCollide();

//        then
        noCollisionsAreDetected(triangles, island);

//        when
        triangles = trianglesOnlyJustCollide();

//        then
        collisionsAreDetected(triangles, island);

    }

    private void collisionsAreDetected(Triangle[] triangles, CompoundIsland island) {
        for (Triangle t : triangles) {
            assertTrue(t.dockablesCollide(IDENTITY_TRANSFORMATION, IDENTITY_TRANSFORMATION, island));
            assertTrue(island.dockablesCollide(IDENTITY_TRANSFORMATION, IDENTITY_TRANSFORMATION, t));
        }
    }

    private Triangle[] trianglesOnlyJustCollide() {
        Triangle t1 = new Triangle(new double[]{3+Triangle.HEIGHT*0.99, 5}, new double[]{0, 0}, Math.PI/2, 0);
        Triangle t2 = new Triangle(new double[]{3-Triangle.HEIGHT*1.99, 5}, new double[]{0, 0}, Math.PI/2, 0);
        Triangle t3 = new Triangle(new double[]{3-2*Triangle.HEIGHT/3, 5 - 0.99}, new double[]{0, 0}, 3*Math.PI/2, 0);
        Triangle t4 = new Triangle(new double[]{3, 5 + 0.99}, new double[]{0, 0}, Math.PI/2, 0);
        return new Triangle[]{t1, t2, t3, t4};
    }

    private void noCollisionsAreDetected(Triangle[] triangles, CompoundIsland island) {
        for (Triangle t : triangles) {
            assertFalse(t.dockablesCollide(IDENTITY_TRANSFORMATION, IDENTITY_TRANSFORMATION, island));
            assertFalse(island.dockablesCollide(IDENTITY_TRANSFORMATION, IDENTITY_TRANSFORMATION, t));
        }
    }

    private Triangle[] trianglesOnlyJustDoNotCollide() {
        Triangle t1 = new Triangle(new double[]{3+Triangle.HEIGHT*1.01, 5}, new double[]{0, 0}, Math.PI/2, 0);
        Triangle t2 = new Triangle(new double[]{3-Triangle.HEIGHT*2.01, 5}, new double[]{0, 0}, Math.PI/2, 0);
        Triangle t3 = new Triangle(new double[]{3-2*Triangle.HEIGHT/3, 5 - 1.01}, new double[]{0, 0}, 3*Math.PI/2, 0);
        Triangle t4 = new Triangle(new double[]{3, 5 + 1.01}, new double[]{0, 0}, Math.PI/2, 0);
        return new Triangle[]{t1, t2, t3, t4};
    }

    private CompoundIsland createCompoundIsland() {
        Triangle t1 = new Triangle(new double[]{3, 5}, new double[]{0, 0}, Math.PI/2, 0);
        Triangle t2 = new Triangle(new double[]{3-2.0/3.0*Triangle.HEIGHT, 5}, new double[]{0, 0}, 3*Math.PI/2, 0);
        CompoundIsland island = new CompoundIsland(t1, t2);
        return island;
    }
}
