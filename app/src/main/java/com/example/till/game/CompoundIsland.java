package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 30.12.15.
 */
public class CompoundIsland implements Dockable {
    private List<Triangle> surface;
    private double[] translation;
    private double[] rotationMatrix;
    private double rotation;

    public List<Triangle> getContent() {
        return content;
    }

    List<Triangle> content;
    private Drawer drawer;
    private List<Triangle> collisionCandidates = new LinkedList<>();

    public CompoundIsland(Triangle t1, Triangle t2) {
        content = new ArrayList<>();
        content.add(t1);
        content.add(t2);
        translation = t1.getTranslation();
        rotation = t1.getRotation();
        rotationMatrix = calculateRotationMatrix(rotation);

        t1.setTranslation(new double[]{0, 0});
        t1.setRotation(0);
        adaptTriangleToIslandCoordinates(t2);

        t1.addNeighbor();
        t2.addNeighbor();

        GameField.getInstance().getContent().add(this);
        GameField.getInstance().getContent().remove(t1);
        GameField.getInstance().getContent().remove(t2);
        surface = content;

        drawer = new CompoundIslandDrawer();
    }

    public List<Triangle> getCollisionCandidate() {
        return collisionCandidates;
    }

    private SimpleGraph<Triangle, Double> g;
    public static final double MAX_DISTANCE_TO_TRIGGER_DOCKING=(2 * Triangle.A[1]) * 1.1;



    @Override
    public double[] getTranslation() {
        return new double[0];
    }

    @Override
    public double[] getMovement() {
        return new double[0];
    }

    @Override
    public double getRotation() {
        return 0;
    }

    @Override
    public double getRotationSpeed() {
        return 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void rollbackUpdate() {

    }

    @Override
    public Dockable focus() {
        return null;
    }

    @Override
    public void unfocus() {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean isInside(double x, double y) {
        return false;
    }

    @Override
    public void handleTap(MotionEvent event) {

    }

    @Override
    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        collisionCandidates.clear();
        transformationThis = concatenateLinearTransformation(transformationThis, translation, rotationMatrix);
        for (Triangle t : surface) {
            if (t.dockablesCollide(transformationThis, transformationDockable, dockable)) {
                collisionCandidates.add(t);
            }
        }
        return collisionCandidates.size()>0;
    }

    @Override
    public void handleCollision(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        if (dockable.getClass().getSimpleName().equals(Triangle.class.getSimpleName())) {
            handleCollision(transformationThis, transformationDockable, (Triangle) dockable);
        } else if (dockable.getClass().getSimpleName().equals(CompoundIsland.class.getSimpleName())) {
            handleCollision(transformationThis, transformationDockable, (CompoundIsland) dockable);
        } else {
            throw new IllegalArgumentException("Not yet implemented for this kind of dockable");
        }
    }

    private void handleCollision(double[] transformationThis, double[] transformationTriangle, Triangle triangle) {
        transformationThis = concatenateLinearTransformation(transformationThis, translation, rotationMatrix);
        for (Triangle candidate : collisionCandidates) {
            if (!candidate.dockablesCollide(transformationThis, transformationTriangle, triangle)) {
                throw new RuntimeException("triangle does not collide with collision candidate");
            }
            double[] vectorBetweenMiddlePoints = substract(transformLinear(transformationTriangle, triangle.getTranslation()),
                    transformLinear(transformationThis, candidate.getTranslation()));
            if (normL2(vectorBetweenMiddlePoints) <= MAX_DISTANCE_TO_TRIGGER_DOCKING) {
                dock(triangle);
                return;
            }
        }

        triangle.setRotation(-triangle.getRotationSpeed());
        triangle.setMovement(scale(triangle.getMovement(), -1));
        setRotationSpeed(-getRotationSpeed());
        setMovement(scale(getMovement(), -1));
    }

    private void dock(Triangle triangle) {
        content.add(triangle);
        surface.add(triangle);
        GameField.getInstance().getContent().remove(triangle);
        adaptTriangleToIslandCoordinates(triangle);
    }

    private void handleCollision(double[] transformationThis, double[] transformationCompoundIsland, CompoundIsland compoundIsland) {
        throw new RuntimeException("Collision between two compoundIslands still needs to be implemented.");
    }


    @Override
    public void setRotationSpeed(double v) {

    }

    @Override
    public void setMovement(double[] scale) {

    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }

    public boolean contains(Triangle t) {
        return content.contains(t);
    }

    private void adaptTriangleToIslandCoordinates(Triangle t) {
        double[] trafoTriangleToField = makeLinearTransformation(t.getTranslation(), calculateRotationMatrix(t.getRotation()));
        double[] trafoThisToField = makeLinearTransformation(translation, rotationMatrix);
        double[] trafoTriangleToThis = concatenateLinearTransformation(invertLinearTransformation(trafoThisToField), trafoTriangleToField);

        t.setTranslation(transformLinear(invertLinearTransformation(trafoThisToField), t.getTranslation()));
        t.setRotation(t.getRotation() - rotation);
    }

    private class CompoundIslandDrawer extends Drawer{

        @Override
        public Canvas draw(double[] transformation, Canvas canvas) {
            transformation = concatenateLinearTransformation(transformation, translation, rotationMatrix);
            for (Triangle t : content) {
                t.getDrawer().draw(transformation, canvas);
            }
            return canvas;
        }
    }
}
