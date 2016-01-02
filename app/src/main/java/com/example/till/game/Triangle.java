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
    private static final int MAX_NUMBER_OF_NEIGHBORS = 3;

    public void setTranslation(double[] translation) {
        this.translation = translation;
    }

    private double[] translation;
    private double[] movement;

    public void setRotation(double rotation) {
        this.rotation = rotation;
        rotationMatrix = calculateRotationMatrix(rotation);
    }

    private double rotation;
    private double rotationSpeed;
    private double[] rotationMatrix;
    private boolean isFocused;
    private double[] positionOfLastTouch;
    public static final double[] A = {-0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    public static final double[] B = {0.5, 2.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    public static final double[] C = {0, -4.0 / 3.0 * Math.sqrt(3.0 / 16.0)};
    private TriangleDrawer drawer;
    private int numberOfNeighbors = 0;
    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed) {
        if (positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException("PositionInParent and Movement must have dimension 2. PositionInParent had dimension " + positionInParent.length + ", movement had dimension " + movement.length + ".");
        }
        this.translation = positionInParent;
        this.movement = movement;
        setRotation(currentRotation);
        this.rotationSpeed = rotationSpeed;
        drawer = new TriangleDrawer();
        update();
    }


    public double[] getPositionOfLastTouch() {
        return positionOfLastTouch;
    }

    private double[] calculatePositionOfCorner(double[] vector) {
        double[] vectorRotated = multiplyMatrixVector(rotationMatrix, vector);
        double[] vectorShifted = add(vectorRotated, translation);
        return vectorShifted;
    }

    @Override
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

    @Override
    public void setMovement(double[] movement) {
        this.movement = movement;
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
    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void update() {
        setRotation(rotation + rotationSpeed);
        translation = add(translation, movement);
    }

    @Override
    public void rollbackUpdate() {
        setRotation(rotation - rotationSpeed);
        translation = substract(translation, movement);
    }

    @Override
    public Dockable focus() {
        isFocused = true;
        GameField.getInstance().setCurrentlyFocusedDockable(this);
        return this;
    }

    @Override
    public void unfocus() {
        isFocused = false;
        GameField.getInstance().setCurrentlyFocusedDockable(null);
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
    @Override
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
        return multiplyMatrixVector(rotationMatrix, translated);
    }

    @Override
    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        if (dockable.getClass().getSimpleName().equals(Triangle.class.getSimpleName())) {
            return trianglesCollide(transformationThis, transformationDockable, (Triangle) dockable);
        } else if (dockable.getClass().getSimpleName().equals(CompoundIsland.class.getSimpleName())) {
            return dockable.dockablesCollide(transformationDockable, transformationThis, this);
        } else {
            throw new IllegalArgumentException("For input dockable collisions are not yet implemented.");
        }
    }

    private boolean trianglesCollide(double[] transformationThis, double[] transformationTriangle, Triangle triangle) {
        transformationThis = concatenateLinearTransformation(transformationThis, translation, rotationMatrix);
        transformationTriangle = concatenateLinearTransformation(transformationTriangle, triangle.translation, triangle.rotationMatrix);
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

    @Override
    public void handleCollision(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        if (Triangle.class.isInstance(dockable)) {
            Triangle triangle = (Triangle) dockable;
            double[] vectorBetweenCenters = substract(transformLinear(transformationThis, translation), transformLinear(transformationDockable, triangle.getTranslation()));
            if (normL2(vectorBetweenCenters)<=CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING){
                new CompoundIsland(this, triangle);
            }
            else{
                repell(triangle);
            }
        } else if (CompoundIsland.class.isInstance(dockable)) {
            ((CompoundIsland) dockable).handleCollision(transformationDockable, transformationThis, this);
        }
    }


    private void repell(Triangle triangle) {
        rollbackUpdate();
        triangle.rollbackUpdate();
        setRotationSpeed(-getRotationSpeed());
        setMovement(VectorCalculations2D.scale(movement, -1));
        triangle.setRotationSpeed(-triangle.getRotationSpeed());
        triangle.setMovement(VectorCalculations2D.scale(triangle.getMovement(), -1));
    }

    private void handleCollision(double[] transformationThis, double[] transformationCompoundIsland, CompoundIsland compoundIsland) {
        rollbackUpdate();
        compoundIsland.rollbackUpdate();
        setRotationSpeed(-getRotationSpeed());
        setMovement(VectorCalculations2D.scale(movement, -1));
        compoundIsland.setRotationSpeed(-compoundIsland.getRotationSpeed());
        compoundIsland.setMovement(VectorCalculations2D.scale(compoundIsland.getMovement(), -1));
    }

    @Override
    public void addNeighbor() {
        if (numberOfNeighbors < 3) {
            numberOfNeighbors++;
        } else {
            throw new RuntimeException("A triangle must not have more than 3 neighbors.");
        }
    }

    @Override
    public void removeNeighbor() {
        if (numberOfNeighbors > 0) {
            numberOfNeighbors--;
        } else {
            throw new RuntimeException("A triangle must not have less than 0 neighbors.");
        }
    }

    @Override
    public int getMaxNumberOfNeighbors() {
        return MAX_NUMBER_OF_NEIGHBORS;
    }

    @Override
    public int getNumberOfNeighbors() {
        return numberOfNeighbors;
    }

    public boolean equals(Triangle triangle) {
        if (!Arrays.equals(translation, triangle.translation)) {
            return false;
        }
        if (!Arrays.equals(movement, triangle.movement)) {
            return false;
        }
        if (rotation != triangle.rotation) {
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
        if (!Arrays.equals(positionOfLastTouch, triangle.positionOfLastTouch)) {
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
            int color = 0;
            if (numberOfNeighbors > 0) {
                color = Color.BLUE;
            }else if (isFocused) {
                color = Color.GREEN;
            } else {
                color = Color.RED;
            }
            paint.setColor(color);
            paint.setAntiAlias(true);
            double[] A = transformLinear(transformationFromTriangle, Triangle.A);
            double[] B = transformLinear(transformationFromTriangle, Triangle.B);
            double[] C = transformLinear(transformationFromTriangle, Triangle.C);
            double[] center = transformLinear(translation, rotationMatrix, new double[]{0, 0});
            double[] insideNearA = transformLinear(translation, rotationMatrix, scale(Triangle.A, 0.99));
            double[] outsideNearA = transformLinear(translation, rotationMatrix, scale(Triangle.A, 1.01));
            double[] insideNearB = transformLinear(translation, rotationMatrix, scale(Triangle.B, 0.99));
            double[] outsideNearB = transformLinear(translation, rotationMatrix, scale(Triangle.B, 1.01));
            double[] insideNearC = transformLinear(translation, rotationMatrix, scale(Triangle.C, 0.99));
            double[] outsideNearC = transformLinear(translation, rotationMatrix, scale(Triangle.C, 1.01));
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
