package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 30.12.15.
 */
public class CompoundIsland implements Dockable {
    private double[] translation;
    private double[] rotationCenterInParent;
    private double[] coordinateBaseToRotationCenter;
    private double[] rotationMatrix;
    private double rotation;
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

        translation = t1.getTranslation();
        rotationCenterInParent = scale(add(t1.getTranslation(), t2.getTranslation()), 0.5);
        coordinateBaseToRotationCenter = substract(rotationCenterInParent, translation);
        setRotation(t1.getRotation());
        setMovement(new double[]{0, 0});
        setRotationSpeed(0.25 * Math.PI/MainThread.MAX_FPS);

        t1.setTranslation(new double[]{0, 0});
        t1.setRotation(0);
        adaptTriangleToIslandCoordinates(t2);

        t1.addNeighbor();
        t2.addNeighbor();

        GameField.getInstance().getContent().add(this);
        GameField.getInstance().getContent().remove(t1);
        GameField.getInstance().getContent().remove(t2);

        drawer = new CompoundIslandDrawer();
    }

    public List<Dockable> getCollisionCandidate() {
        return collisionCandidates;
    }

    private SimpleGraph<Triangle, Double> g;
    public static final double MAX_DISTANCE_TO_TRIGGER_DOCKING=(2 * Triangle.A[1]) * 1.1;



    @Override
    public double[] getTranslation() {
        return translation;
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
    public void handleTap(MotionEvent event) {
        throw new RuntimeException("Not yet implemented.");

    }

    @Override
    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        collisionCandidates.clear();
        transformationThis = concatenateLinearTransformation(transformationThis, translation, rotationMatrix);
        for (Dockable d : surface) {
            if (d.dockablesCollide(transformationThis, transformationDockable, dockable)) {
                collisionCandidates.add(d);
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
        for (Dockable candidate : collisionCandidates) {
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
    }

    private boolean areNeighbors(Dockable d1, Dockable d2) {
        if (Triangle.class.isInstance(d1) && Triangle.class.isInstance(d2)) {
            return normL2(substract(d1.getTranslation(), d2.getTranslation())) <= MAX_DISTANCE_TO_TRIGGER_DOCKING;
        } else {
            throw new RuntimeException("Not yet implemented for inputs different than Triangles");
        }
    }

    private void handleCollision(double[] transformationThis, double[] transformationCompoundIsland, CompoundIsland compoundIsland) {
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
        double[] trafoThisToField = makeLinearTransformation(substract(rotationCenterInParent, coordinateBaseToRotationCenter), rotationMatrix);

        double newRotationUnsmoothed = t.getRotation() - rotation;
        long nRotationSteps = Math.round(newRotationUnsmoothed / (Math.PI / 3));
        nRotationSteps = Math.abs(nRotationSteps%2);
        t.setRotation(nRotationSteps*Math.PI);

        double[] translation = transformLinear(invertLinearTransformation(trafoThisToField), t.getTranslation());
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
        t.setTranslation(translation);
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        rotationMatrix = calculateRotationMatrix(rotation);
    }

    private class CompoundIslandDrawer extends Drawer{

        @Override
        public Canvas draw(double[] transformation, Canvas canvas) {
            transformation = concatenateLinearTransformation(transformation, translation, rotationMatrix);
            for (Dockable d : content.vertexSet()) {
                d.getDrawer().draw(transformation, canvas);
            }
            return canvas;
        }
    }
}
