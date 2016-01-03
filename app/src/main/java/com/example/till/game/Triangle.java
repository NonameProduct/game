package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Arrays;

import static com.example.till.game.VectorCalculations2D.*;

/**
 * Created by till on 23.12.15.
 */
public class Triangle extends Island implements Drawable{

    public static final double HEIGHT = Math.sqrt(3.0/4.0);
    public static final double[] A = {-0.5, 1.0 / 3.0 * HEIGHT};
    public static final double[] B = {0.5, 1.0 / 3.0 * HEIGHT};
    public static final double[] C = {0, -2.0 / 3.0 * HEIGHT};
    private static final String TAG = Triangle.class.getSimpleName();
    public static final int MAX_NUMBER_OF_NEIGHBORS = 3;
    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed) {
        if (positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException("PositionInParent and Movement must have dimension 2. PositionInParent had dimension " + positionInParent.length + ", movement had dimension " + movement.length + ".");
        }
        this.parentToCenter = positionInParent;
        this.movement = movement;
        setRotation(currentRotation);
        this.rotationSpeed = rotationSpeed;
        drawer = new TriangleDrawer();
        update();
    }

    /**
     * Parameters are given in coordinates of the hoding parent. They need to be transformed into triangle coordinates first.
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean isInside(double x, double y) {
        double[] triangleCoordinates = transformLinear(invertLinearTransformation(parentToCenter, rotationMatrix), new double[]{x, y});
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

    @Override
    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Island island) {
        if (island.getClass().getSimpleName().equals(Triangle.class.getSimpleName())) {
            return trianglesCollide(transformationThis, transformationDockable, (Triangle) island);
        } else if (island.getClass().getSimpleName().equals(CompoundIsland.class.getSimpleName())) {
            return island.dockablesCollide(transformationDockable, transformationThis, this);
        } else {
            throw new IllegalArgumentException("For input island collisions are not yet implemented.");
        }
    }

    private boolean trianglesCollide(double[] transformationThis, double[] transformationTriangle, Triangle triangle) {
        transformationThis = concatLinearTransformation(transformationThis, parentToCenter, rotationMatrix);
        transformationTriangle = concatLinearTransformation(transformationTriangle, triangle.parentToCenter, triangle.rotationMatrix);
        double[] transformationTriangleToThis = concatLinearTransformation(invertLinearTransformation(transformationThis), transformationTriangle);
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
    public void handleCollision(double[] transformationThis, double[] transformationDockable, Island island) {
        if (Triangle.class.isInstance(island)) {
            Triangle triangle = (Triangle) island;
            double[] vectorBetweenCenters = substract(transformLinear(transformationThis, parentToCenter), transformLinear(transformationDockable, triangle.getParentToCenter()));
            if (normL2(vectorBetweenCenters)<=CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING){
                new CompoundIsland(this, triangle);
            }
            else{
                repell(triangle);
            }
        } else if (CompoundIsland.class.isInstance(island)) {
            ((CompoundIsland) island).handleCollision(transformationDockable, transformationThis, this);
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


    public boolean equals(Triangle triangle) {
        if (!Arrays.equals(parentToCenter, triangle.parentToCenter)) {
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
        return true;
    }

    @Override
    public void addNeighbor(){
        super.addNeighbor();
        if (getNumberOfNeighbors() > getMaxNumberOfNeighbors()) {
            throw new RuntimeException("For triangles, the maximal number of Neighbors is 3.");
        }
    }

    @Override
    public int getMaxNumberOfNeighbors() {
        return 3;
    }

    private class TriangleDrawer extends Drawer{
        @Override
        public Canvas draw(double[] transformationToUserInterface, Canvas canvas) {
            double[] transformationFromTriangle = concatLinearTransformation(transformationToUserInterface, makeLinearTransformation(parentToCenter, rotationMatrix));
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
