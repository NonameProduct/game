package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 30.12.15.
 */
public class CompoundIsland implements Dockable {
    private double[] parentToCenter; // Center is the center of the rotation.
    private double[] transformationBaseToCenter;
    private double[] rotationMatrixCenter;
    private double rotation;
    double[] transformationThis;

    private double rotationSpeed;
    double[] movement;

    public Set<Dockable> getContent() {
        return content.vertexSet();
    }

    Graph<Dockable, DefaultEdge> content;
    private List<Dockable> surface;
    private Drawer drawer;
    private List<Dockable> collisionCandidates = new LinkedList<>();

    public CompoundIsland(Triangle t1, Triangle t2) {
        content = new SimpleGraph<Dockable, DefaultEdge>(DefaultEdge.class);
        content.addVertex(t1);
        content.addVertex(t2);
        content.addEdge(t1, t2);
        surface = new ArrayList<>();
        surface.add(t1);
        surface.add(t2);

        double[] base = t1.getParentToCenter();
        parentToCenter = scale(add(t1.getParentToCenter(), t2.getParentToCenter()), 0.5);
        double[] centerToBase = substract(base, parentToCenter);
        double[] rotationMatrixBase = calculateRotationMatrix(t1.getRotation());
        transformationBaseToCenter = makeLinearTransformation(centerToBase, rotationMatrixBase);
        setRotation(0);
        updateTransformationThis();
        setMovement(new double[]{0, 0});
        setRotationSpeed(0.25 * Math.PI/MainThread.MAX_FPS);

        t1.setParentToCenter(new double[]{0, 0});
        t1.setRotation(0);
        adaptTriangleToIslandCoordinates(t2);

        t1.addNeighbor();
        t2.addNeighbor();

        GameField.getInstance().getContent().add(this);
        GameField.getInstance().getContent().remove(t1);
        GameField.getInstance().getContent().remove(t2);

        drawer = new CompoundIslandDrawer();
    }



    private void updateTransformationThis() {
        transformationThis = concatLinearTransformation(parentToCenter, rotationMatrixCenter, transformationBaseToCenter);
    }

    public List<Dockable> getCollisionCandidate() {
        return collisionCandidates;
    }

    private SimpleGraph<Triangle, Double> g;
    public static final double MAX_DISTANCE_TO_TRIGGER_DOCKING=(2 * Triangle.A[1]) * 1.1;



    @Override
    public double[] getParentToCenter() {
        return parentToCenter;
    }

    private void setParentToCenter(double[] translation) {
        parentToCenter = translation;
        updateTransformationThis();
    }

    @Override
    public double[] getMovement() {
        return movement;
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public double getRotationSpeed() {
        return rotationSpeed;
    }

    @Override
    public void update() {
        setRotation(rotation + rotationSpeed);
        setParentToCenter(add(parentToCenter, movement));
    }

    @Override
    public void rollbackUpdate() {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public Dockable focus() {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public void unfocus() {
        throw new RuntimeException("Not yet implemented.");

    }

    @Override
    public boolean isFocused() {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public boolean isInside(double x, double y) {
        throw new RuntimeException("Not yet implemented.");
    }


    @Override
    public boolean dockablesCollide(double[] transformationParent, double[] transformationDockable, Dockable dockable) {
        collisionCandidates.clear();;
        double[] transformationToGameField = concatLinearTransformation(transformationParent, transformationThis);
        for (Dockable d : surface) {
            if (d.dockablesCollide(transformationToGameField, transformationDockable, dockable)) {
                collisionCandidates.add(d);
            }
        }
        return collisionCandidates.size()>0;
    }

    @Override
    public void handleCollision(double[] transformationParent, double[] transformationDockable, Dockable dockable) {
        if (Triangle.class.isInstance(dockable)) {
            handleCollision(transformationParent, transformationDockable, (Triangle) dockable);
        } else if (CompoundIsland.class.isInstance(dockable)) {
            handleCollision(transformationParent, transformationDockable, (CompoundIsland) dockable);
        } else {
            throw new IllegalArgumentException("Not yet implemented for this kind of dockable");
        }
    }

    private void handleCollision(double[] transformationParent, double[] transformationTriangle, Triangle triangle) {
        double[] transformationThisToGameField = concatLinearTransformation(transformationParent, transformationThis);
        for (Dockable candidate : collisionCandidates) {
            if (!candidate.dockablesCollide(transformationThisToGameField, transformationTriangle, triangle)) {
                throw new RuntimeException("triangle does not collide with collision candidate");
            }
            double[] vectorBetweenMiddlePoints = substract(transformLinear(transformationTriangle, triangle.getParentToCenter()),
                    transformLinear(transformationThisToGameField, candidate.getParentToCenter()));
            if (normL2(vectorBetweenMiddlePoints) <= MAX_DISTANCE_TO_TRIGGER_DOCKING) {
                dock(triangle);
                return;
            }
        }

        triangle.setRotationSpeed(-triangle.getRotationSpeed());
        triangle.setMovement(scale(triangle.getMovement(), -1));
        setRotationSpeed(-getRotationSpeed());
        setMovement(scale(getMovement(), -1));
    }

    private void dock(Triangle triangle) {
        content.addVertex(triangle);
        GameField.getInstance().getContent().remove(triangle);
        adaptTriangleToIslandCoordinates(triangle);
        List<Dockable> toRemoveFromSurface = new ArrayList<>();
        for (Dockable d : surface) {
            if(areNeighbors(triangle, d)){
                d.addNeighbor();
                triangle.addNeighbor();
                content.addEdge(d, triangle);
            }
            if (d.getMaxNumberOfNeighbors() >= d.getNumberOfNeighbors()) {
                toRemoveFromSurface.add(d);
            }
        }
        surface.remove(toRemoveFromSurface);
        surface.add(triangle);

        double[] newParentToCenter = scale(add(scale(parentToCenter, content.vertexSet().size() - 1), transformLinear(transformationThis, triangle.getParentToCenter())), 1.0/content.vertexSet().size());
        changeParentToCenter(newParentToCenter);
        updateTransformationThis();

    }

    private void changeParentToCenter(double[] newParentToCenter) {
        transformationBaseToCenter = concatLinearTransformation(
                scale(newParentToCenter, -1), new double[]{1, 0, 0, 1},
                concatLinearTransformation(parentToCenter, rotationMatrixCenter, transformationBaseToCenter));
        rotation = 0;
        rotationMatrixCenter = calculateRotationMatrix(0);
        parentToCenter = newParentToCenter;
        updateTransformationThis();
    }

    private boolean areNeighbors(Dockable d1, Dockable d2) {
        if (Triangle.class.isInstance(d1) && Triangle.class.isInstance(d2)) {
            return normL2(substract(d1.getParentToCenter(), d2.getParentToCenter())) <= MAX_DISTANCE_TO_TRIGGER_DOCKING;
        } else {
            throw new RuntimeException("Not yet implemented for inputs different than Triangles");
        }
    }

    private void handleCollision(double[] transformationParent, double[] transformationCompoundIsland, CompoundIsland compoundIsland) {
        throw new RuntimeException("Collision between two compoundIslands still needs to be implemented.");
    }


    @Override
    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;

    }

    @Override
    public void setMovement(double[] movement) {
        this.movement = movement;

    }

    @Override
    public void addNeighbor() {
        throw new RuntimeException("Not yet implemented.");

    }

    @Override
    public void removeNeighbor() {
        throw new RuntimeException("Not yet implemented.");

    }

    @Override
    public int getMaxNumberOfNeighbors() {
        throw new RuntimeException("Method not yet implemented");
    }

    @Override
    public int getNumberOfNeighbors() {
        throw new RuntimeException("Method not yet implemented");
    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }

    public boolean contains(Triangle t) {
        return content.containsVertex(t);
    }

    private void adaptTriangleToIslandCoordinates(Triangle t) {

        double newRotationUnsmoothed = t.getRotation() - rotation - getRotationAngle(Arrays.copyOfRange(transformationBaseToCenter, 2, 6));
        long nRotationSteps = Math.round(newRotationUnsmoothed / (Math.PI / 3));
        nRotationSteps = Math.abs(nRotationSteps%2);
        t.setRotation(nRotationSteps*Math.PI);

        double[] translation = transformLinear(invertLinearTransformation(transformationThis), t.getParentToCenter());
        translation[0] = Math.round(translation[0]*2.0)/2.0;
        if (t.getRotation() == 0) {
            translation[1] = Math.round(translation[1]/Triangle.HEIGHT) * Triangle.HEIGHT;
        }else if (t.getRotation() == Math.PI) {
            double d = translation[1] + Triangle.HEIGHT*1.0/3.0;
            d = Math.round(d/Triangle.HEIGHT)*Triangle.HEIGHT;
            translation[1] = d-Triangle.HEIGHT*1.0/3.0;
        } else {
            throw new RuntimeException("Triangle has incorrect angle. Must have either 0 or PI. Has: "+ t.getRotation() + ".");
        }
        t.setParentToCenter(translation);
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        rotationMatrixCenter = calculateRotationMatrix(rotation);
        updateTransformationThis();
    }

    private class CompoundIslandDrawer extends Drawer{

        @Override
        public Canvas draw(double[] transformation, Canvas canvas) {
            transformation = concatLinearTransformation(transformation, transformationThis);
            for (Dockable d : content.vertexSet()) {
                d.getDrawer().draw(transformation, canvas);
            }
            return canvas;
        }
    }
}
