package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {
    private static final String TAG = Triangle.class.getSimpleName();
    private double[] movement;
    private double currentRotation;
    private double rotationSpeed;
    private double[] rotationMatrix;
    private boolean isFocused;

    public int getCurrentColor() {
        return currentColor;
    }

    private int currentColor;

    public double[] getPositionOfLastTouch() {
        return positionOfLastTouch;
    }

    private double[] positionOfLastTouch;

    public double[] getPositionInParentA() {
        return positionInParentA;
    }

    public double[] getPositionInParentB() {
        return positionInParentB;
    }

    public double[] getPositionInParentC() {
        return positionInParentC;
    }

    private double[] positionInParentA;
    private double[] positionInParentB;
    private double[] positionInParentC;
    double[] positionInParent;

    private double[] cornerRelativeToCenterA = {-0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    private double[] cornerRelativeToCenterB = {0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    private double[] cornerRelativeToCenterC = {0, -4.0 / 3.0 * Math.sqrt(3.0 / 16.0)};

    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed) {
        if (positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException();
        }
        this.positionInParent = positionInParent;
        this.movement = movement;
        this.currentRotation = currentRotation;
        this.rotationSpeed = rotationSpeed;
        currentColor = Color.RED;

        Log.i("game_log", "sqrt(3/16): " + Math.sqrt(3 / 16));
        update();
    }


    private double[] calculatePositionOfCorner(double[] vector) {
        double[] vectorScaled = scale(vector, 200);
        double[] rotationMatrix = {Math.cos(currentRotation), -Math.sin(currentRotation),
                Math.sin(currentRotation), Math.cos(currentRotation)};
        double[] vectorRotated = multiplyMatrixVector(rotationMatrix, vectorScaled);
        double[] vectorShifted = add(vectorRotated, positionInParent);
        return vectorShifted;
    }

    public void handleTap(MotionEvent event) {
        if (isInside(event.getX(), event.getY())) {
            focus();
        } else {
            unfocus();
        }
        positionOfLastTouch = new double[]{event.getX(), event.getY()};
    }

    @Override
    public double[] getPositionInParent() {
        return positionInParent;
    }

    @Override
    public double[] getMovement() {
        return movement;
    }

    public void setMovement(double[] movement) {
        this.movement = movement;
    }

    @Override
    public double getCurrentRotation() {
        return currentRotation;
    }

    @Override
    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public void update() {
        currentRotation += rotationSpeed;
        positionInParent = add(positionInParent, movement);
        positionInParentA = calculatePositionOfCorner(cornerRelativeToCenterA);
        positionInParentB = calculatePositionOfCorner(cornerRelativeToCenterB);
        positionInParentC = calculatePositionOfCorner(cornerRelativeToCenterC);
    }

    @Override
    public Dockable focus() {
        isFocused = true;
        currentColor = Color.GREEN;
        GameField.getInstance().setCurrentlyFocusedDockable(this);
        return this;
    }

    public void unfocus() {
        isFocused = false;
        GameField.getInstance().setCurrentlyFocusedDockable(null);
        currentColor = Color.RED;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (!isFocused()) {
            throw new IllegalStateException("Triangle.onFling() should only be called if the respective triangle currantly isFocused()");
        }

        double[] startPoint = {event1.getX(), event1.getY()};
        double[] startPointInTriangleCoordinates = transformToTriangleCoordinates(startPoint);
        double[] endPoint = {event2.getX(), event2.getY()};
        double[] endPointInTriangleCoordinates = transformToTriangleCoordinates(endPoint);

        if (normL2(startPointInTriangleCoordinates) < normL2(cornerRelativeToCenterA) * 200) {
            setMovement(new double[]{velocityX / (10.0 * MainThread.MAX_FPS), velocityY / (30.0 * MainThread.MAX_FPS)});
        } else if (normL2(startPointInTriangleCoordinates) < normL2(cornerRelativeToCenterA) * 3 * 200) {
            double determinantOfDirectionMatrix = startPointInTriangleCoordinates[0] * endPointInTriangleCoordinates[1]
                    - startPointInTriangleCoordinates[1] * endPointInTriangleCoordinates[0];
            int signOfDeterminant;
            if (determinantOfDirectionMatrix > 0) {
                signOfDeterminant = 1;
            } else {
                signOfDeterminant = -1;
            }
            rotationSpeed += signOfDeterminant * 2 * Math.PI / (MainThread.MAX_FPS * 4);
        }
        return false;
    }

    public boolean isInside(double x, double y) {
        double[] vector = substract(new double[]{x, y}, positionInParentA);
        double[] axis1 = substract(positionInParentB, positionInParentA);
        double[] axis2 = substract(positionInParentC, positionInParentA);
        double a = axis1[0];
        double b = axis2[0];
        double c = axis1[1];
        double d = axis2[1];
        double[] invertedBaseMatrix = invert(new double[]{a, b, c, d});
        double[] baseCoordinates = multiplyMatrixVector(invertedBaseMatrix, vector);
        Log.d(TAG, "Base coordinates: x=" + baseCoordinates[0] + ",y=" + baseCoordinates[1]);

        return (baseCoordinates[0] > 0 && baseCoordinates[1] > 0 && baseCoordinates[0] + baseCoordinates[1] <= 1);
    }

    public boolean isInside(double[] vector) {
        return isInside(vector[0], vector[1]);
    }

    private double[] transformToTriangleCoordinates(double[] coordinates) {
        double[] translated = substract(coordinates, positionInParent);
        double[] rotationMatrix = {Math.cos(currentRotation), -Math.sin(currentRotation),
                Math.sin(currentRotation), Math.cos(currentRotation)};
        double[] rotated = multiplyMatrixVector(rotationMatrix, translated);
        return rotated;
    }

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

}
