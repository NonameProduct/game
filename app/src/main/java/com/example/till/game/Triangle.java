package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.Arrays;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable, Drawable{

    private static final String TAG = Triangle.class.getSimpleName();
    double[] translation;
    private double[] movement;
    private double currentRotation;
    private double rotationSpeed;
    private double[] rotationMatrix;
    private boolean isFocused;
    private int currentColor;
    private double[] positionOfLastTouch;
    public static final double[] A = {-0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    public static final double[] B = {0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    public static final double[] C = {0, -4.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    private TriangleDrawer drawer;
    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed) {
        if (positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException();
        }
        this.translation = positionInParent;
        this.movement = movement;
        this.currentRotation = currentRotation;
        this.rotationSpeed = rotationSpeed;
        currentColor = Color.RED;
        drawer = new TriangleDrawer();
        update();
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public double[] getPositionOfLastTouch() {
        return positionOfLastTouch;
    }

    private double[] calculatePositionOfCorner(double[] vector) {
        double[] vectorRotated = multiplyMatrixVector(rotationMatrix, vector);
        double[] vectorShifted = add(vectorRotated, translation);
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
    public Drawer getDrawer() {
        return drawer;
    }

    @Override
    public double[] getTranslation() {
        return translation;
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
        rotationMatrix = new double[]{Math.cos(currentRotation), -Math.sin(currentRotation),
                Math.sin(currentRotation), Math.cos(currentRotation)};
        translation = add(translation, movement);
    }

    @Override
    public void rollbackUpdate() {
        currentRotation -= rotationSpeed;
        rotationMatrix = new double[]{Math.cos(currentRotation), -Math.sin(currentRotation),
                Math.sin(currentRotation), Math.cos(currentRotation)};
        translation = substract(translation, movement);
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
    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        if (!isFocused()) {
            throw new IllegalStateException("Triangle.onFling() should only be called if the respective triangle currantly isFocused()");
        }

        double[] startPoint = {event1x, event1y};
        double[] startPointInTriangleCoordinates = transformToTriangleCoordinates(startPoint);
        double[] endPoint = {event2x, event2y};
        double[] endPointInTriangleCoordinates = transformToTriangleCoordinates(endPoint);

        if (normL2(startPointInTriangleCoordinates) < normL2(A)) {
            setMovement(new double[]{velocityX / (10.0 * MainThread.MAX_FPS), velocityY / (30.0 * MainThread.MAX_FPS)});
        } else if (normL2(startPointInTriangleCoordinates) < normL2(A) * 3) {
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

    /**
     * Parameters are given in coordinates of the hoding parent. They need to be transformed into triangle coordinates first.
     * @param x
     * @param y
     * @return
     */
    public boolean isInside(double x, double y) {
        double[] triangleCoordinates = transformLinear(invertLinearTransformation(translation, rotationMatrix), new double[]{x, y});
        double newX = triangleCoordinates[0];
        double newY = triangleCoordinates[1];
        double[] vectorA = substract(new double[]{newX, newY}, A);
        double[] ab = substract(B, A);
        double[] vectorB = substract(new double[]{newX, newY}, B);
        double[] bc = substract(C, B);
        double[] vectorC = substract(new double[]{newX, newY}, C);
        double[] ca = substract(A, C);
        double determinante1 = VectorCalculations2D.determinante(vectorA, ab);
        double determinante2 = VectorCalculations2D.determinante(vectorB, bc);
        double determinante3 = VectorCalculations2D.determinante(vectorC, ca);
        return determinante1 >= 0 && determinante2 >= 0 && determinante3 >= 0;
    }

    public boolean isInside(double[] vector) {
        return isInside(vector[0], vector[1]);
    }

    private double[] transformToTriangleCoordinates(double[] coordinates) {
        double[] translated = substract(coordinates, translation);
        double[] rotated = multiplyMatrixVector(rotationMatrix, translated);
        return rotated;
    }

    public boolean trianglesCollide(double[] transformationThis, double[] transformationTriangle, Triangle triangle) {
        transformationThis = concatenateLinearTransformation(transformationThis, makeLinearTransformation(translation, rotationMatrix));
        transformationTriangle = concatenateLinearTransformation(transformationTriangle, makeLinearTransformation(triangle.translation, triangle.rotationMatrix));
        double[] transformationTriangleToThis = concatenateLinearTransformation(invertLinearTransformation(transformationThis), transformationTriangle);
        double[][] vertices = new double[][]{A, B, C,
                transformLinear(transformationTriangleToThis, A), transformLinear(transformationTriangleToThis, B), transformLinear(transformationTriangleToThis, C)};
        for (int i = 0; i < 6; i++) {
            double[] pivotVertex1 = VectorCalculations2D.substract(vertices[(i / 3) * 3 + 0], vertices[(1 - i / 3) * 3 + i % 3]);
            double[] pivotVertex2 = VectorCalculations2D.substract(vertices[(i / 3) * 3 + 1], vertices[(1 - i / 3) * 3 + i % 3]);
            double[] pivotVertex3 = VectorCalculations2D.substract(vertices[(i / 3) * 3 + 2], vertices[(1 - i / 3) * 3 + i % 3]);
            double[] comparisonEdge = VectorCalculations2D.substract(vertices[(1 - i / 3) * 3 + (i + 1) % 3], vertices[(1 - i / 3) * 3 + i % 3]);
            if (determinante(pivotVertex1, comparisonEdge) < 0 && determinante(pivotVertex2, comparisonEdge) < 0 && determinante(pivotVertex3, comparisonEdge) < 0) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(Triangle triangle) {
        if (!Arrays.equals(translation, triangle.translation)) {
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
        if (!Arrays.equals(A, triangle.A)) {
            return false;
        }
        if (!Arrays.equals(B, triangle.B)) {
            return false;
        }
        if (!Arrays.equals(C, triangle.C)) {
            return false;
        }
        return true;
    }

    private class TriangleDrawer extends Drawer{
        @Override
        public Canvas draw(double[] transformationToUserInterface, Canvas canvas) {
            double[] transformationFromTriangle = concatenateLinearTransformation(transformationToUserInterface, makeLinearTransformation(translation, rotationMatrix));
            Paint paint = new Paint();
            paint.setStrokeWidth(4);
            paint.setColor(currentColor);
            paint.setAntiAlias(true);
            double[] A = transformLinear(transformationFromTriangle, Triangle.this.A);
            double[] B = transformLinear(transformationFromTriangle, Triangle.this.B);
            double[] C = transformLinear(transformationFromTriangle, Triangle.this.C);
            double[] center = transformLinear(translation, rotationMatrix, new double[]{0, 0});
            double[] insideNearA = transformLinear(translation, rotationMatrix, scale(Triangle.this.A, 0.99));
            double[] outsideNearA = transformLinear(translation, rotationMatrix, scale(Triangle.this.A, 1.01));
            double[] insideNearB = transformLinear(translation, rotationMatrix, scale(Triangle.this.B, 0.99));
            double[] outsideNearB = transformLinear(translation, rotationMatrix, scale(Triangle.this.B, 1.01));
            double[] insideNearC = transformLinear(translation, rotationMatrix, scale(Triangle.this.C, 0.99));
            double[] outsideNearC = transformLinear(translation, rotationMatrix, scale(Triangle.this.C, 1.01));
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo((float) A[0], (float) (A[1]));
            path.lineTo((float) B[0], (float) (B[1]));
            path.lineTo((float) C[0], (float) (C[1]));
            path.lineTo((float) A[0], (float) (A[1]));
            path.close();

            canvas.drawPath(path, paint);

            return canvas;
        }
    }


}
