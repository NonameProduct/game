package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Arrays;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {

    private static final String TAG = Triangle.class.getSimpleName();
    double[] positionInParent;
    private double[] movement;
    private double currentRotation;
    private double rotationSpeed;
    private double[] rotationMatrix;
    private boolean isFocused;
    private int currentColor;
    private double[] positionOfLastTouch;
    private double[] positionInParentA;
    private double[] positionInParentB;
    private double[] positionInParentC;
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

    public int getCurrentColor() {
        return currentColor;
    }

    public double[] getPositionOfLastTouch() {
        return positionOfLastTouch;
    }

    public double[] getPositionInParentA() {
        return positionInParentA;
    }

    public double[] getPositionInParentB() {
        return positionInParentB;
    }

    public double[] getPositionInParentC() {
        return positionInParentC;
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

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void update() {
        currentRotation += rotationSpeed;
        positionInParent = add(positionInParent, movement);
        positionInParentA = calculatePositionOfCorner(cornerRelativeToCenterA);
        positionInParentB = calculatePositionOfCorner(cornerRelativeToCenterB);
        positionInParentC = calculatePositionOfCorner(cornerRelativeToCenterC);
    }

    @Override
    public void rollbackUpdate() {
        currentRotation -= rotationSpeed;
        positionInParent = substract(positionInParent, movement);
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
        double[] vectorA = substract(new double[]{x, y}, positionInParentA);
        double[] ab = substract(positionInParentB, positionInParentA);
        double[] vectorB = substract(new double[]{x, y}, positionInParentB);
        double[] bc = substract(positionInParentC, positionInParentB);
        double[] vectorC = substract(new double[]{x, y}, positionInParentC);
        double[] ca = substract(positionInParentA, positionInParentC);
        double determinante1 = VectorCalculations2D.determinante(vectorA, ab);
        double determinante2 = VectorCalculations2D.determinante(vectorB, bc);
        double determinante3 = VectorCalculations2D.determinante(vectorC, ca);
        return determinante1>=0 && determinante2 >= 0 && determinante3 >= 0;
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

    public boolean trianglesCollide(Triangle triangle) {
//        double[][] vertices1 = new double[][]{positionInParentA, positionInParentB, positionInParentC};
//        double[][] vertices2 = new double[][]{triangle.getPositionInParentA(), triangle.getPositionInParentB(), triangle.getPositionInParentC()};
        double[][] vertices = new double[][] {positionInParentA, positionInParentB, positionInParentC,
                triangle.getPositionInParentA(), triangle.getPositionInParentB(), triangle.getPositionInParentC()};
        for (int i = 0; i < 6; i++) {
            double[] pivotVertex1 = VectorCalculations2D.substract(vertices[(i/3)*3 + 0], vertices[( 1 - i / 3 ) * 3 + i % 3]);
            double[] pivotVertex2 = VectorCalculations2D.substract(vertices[(i/3)*3 + 1], vertices[( 1 - i / 3 ) * 3 + i%3]);
            double[] pivotVertex3 = VectorCalculations2D.substract(vertices[(i/3)*3 + 2], vertices[( 1 - i / 3 ) * 3 + i%3]);
            double[] comparisonEdge = VectorCalculations2D.substract(vertices[( 1 - i / 3 ) * 3 + (i+1)%3], vertices[( 1 - i / 3 ) * 3 + i%3]);
            if (determinante(pivotVertex1, comparisonEdge) < 0 && determinante(pivotVertex2, comparisonEdge) < 0 && determinante(pivotVertex3, comparisonEdge) < 0) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(Triangle triangle) {
        if (!Arrays.equals(positionInParent, triangle.positionInParent)) {
            return false;
        }
        if (!Arrays.equals(movement, triangle.movement)) {
            return false;
        }
        if (currentRotation != triangle.currentRotation) {
            return false;
        }
        if (rotationSpeed != triangle.rotationSpeed) {
            return false;
        }
        if (!Arrays.equals(rotationMatrix, triangle.rotationMatrix)) {
            return false;
        }
        if (isFocused != triangle.isFocused) {
            return false;
        }
        if (currentColor != triangle.currentColor) {
            return false;
        }
        if (!Arrays.equals(positionOfLastTouch, triangle.positionOfLastTouch)) {
            return false;
        }
        if (!Arrays.equals(positionInParentA, triangle.positionInParentA)) {
            return false;
        }
        if (!Arrays.equals(positionInParentB, triangle.positionInParentB)) {
            return false;
        }
        if (!Arrays.equals(positionInParentC, triangle.positionInParentC)) {
            return false;
        }
        if (!Arrays.equals(cornerRelativeToCenterA, triangle.cornerRelativeToCenterA)) {
            return false;
        }
        if (!Arrays.equals(cornerRelativeToCenterB, triangle.cornerRelativeToCenterB)) {
            return false;
        }
        if (!Arrays.equals(cornerRelativeToCenterC, triangle.cornerRelativeToCenterC)) {
            return false;
        }
        return true;
    }

}
