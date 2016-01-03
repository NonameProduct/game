package com.example.till.game;

import java.io.Serializable;

import static com.example.till.game.VectorCalculations2D.*;


/**
 * Center refers to the rotation center of the object.
 * Created by till on 23.12.15.
 */
public abstract class Island implements Drawable, Serializable {
    protected double rotation;
    protected double[] rotationMatrix;
    protected double rotationSpeed;
    protected double[] parentToCenter; // Center is the center of the rotation.
    protected double[] movement;
    protected Drawer drawer;
    protected boolean isFocused;
    protected int numberOfNeighbors = 0;

    public double[] getParentToCenter(){return parentToCenter;};

    public double[] getMovement() {
        return movement;
    }

    public void setMovement(double[] movement) {
        this.movement = movement;
    }

    public double getRotation(){return rotation;}

    public double getRotationSpeed(){return rotationSpeed;};

    public void setRotationSpeed(double rotationSpeed){this.rotationSpeed = rotationSpeed;}

    public void update() {
        setRotation(rotation + rotationSpeed);
        setParentToCenter(add(parentToCenter, movement));
    }

    public void rollbackUpdate() {
        setRotation(rotation - rotationSpeed);
        setParentToCenter(substract(parentToCenter, movement));
    }

    public Island focus() {
        isFocused = true;
        GameField.getInstance().setCurrentlyFocusedIsland(this);
        return this;
    }

    public void unfocus() {
        isFocused = false;
        GameField.getInstance().setCurrentlyFocusedIsland(null);
    }

    public boolean isFocused() {
        return isFocused;
    }

    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        if (!isFocused()) {
            throw new IllegalStateException("Triangle.onFling() should only be called if the respective triangle currantly isFocused()");
        }

        double[] transformationToTriangle = invertLinearTransformation(parentToCenter, rotationMatrix);
        double[] startPoint = {event1x, event1y};
        double[] startPointInTriangleCoordinates = transformLinear(transformationToTriangle, startPoint);
        double[] endPoint = {event2x, event2y};
        double[] endPointInTriangleCoordinates = transformLinear(transformationToTriangle, endPoint);

        if (normL2(startPointInTriangleCoordinates) < normL2(Triangle.A)) {
            double[] movementIncrement =scale(new double[]{(event2x - event1x), (event2y - event1y)}, 1/(MainThread.MAX_FPS*3.0));
            setMovement(add(movement, movementIncrement));
        } else if (normL2(startPointInTriangleCoordinates) < normL2(Triangle.A) * 3) {
            double determinantOfDirectionMatrix = startPointInTriangleCoordinates[0] * endPointInTriangleCoordinates[1]
                    - startPointInTriangleCoordinates[1] * endPointInTriangleCoordinates[0];
            int signOfDeterminant = (int) Math.signum(determinantOfDirectionMatrix);
            rotationSpeed += signOfDeterminant * normL2(substract(new double[]{event1x, event1y}, new double[]{event2x, event2y}))/(3.0*MainThread.MAX_FPS);
        }
        return false;
    }

    public abstract boolean isInside(double x, double y);

    public abstract boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Island island);

    public abstract void handleCollision(double[] transformationThis, double[] transformationDockable, Island island);


    public void addNeighbor() {
        numberOfNeighbors++;
    }

    public void removeNeighbor() {
        if (numberOfNeighbors > 0) {
            numberOfNeighbors--;
        } else {
            throw new RuntimeException("A triangle must not have less than 0 neighbors.");
        }
    }


    public int getNumberOfNeighbors() {
        return numberOfNeighbors;
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        rotationMatrix = calculateRotationMatrix(rotation);
    }

    public void setParentToCenter(double[] parentToCenter) {
        this.parentToCenter = parentToCenter;
    }

    public abstract int getMaxNumberOfNeighbors();
}
