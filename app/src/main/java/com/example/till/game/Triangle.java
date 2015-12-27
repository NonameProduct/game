package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {
    private static final String TAG = Triangle.class.getSimpleName();
    RealVector movement;
    double currentRotation;
    double rotationSpeed;
    private RealMatrix rotationMatrix;
    private boolean isFocused;
    int currentColor;
    private GameField gameField;

    private RealVector positionOfLastTouch;

    private RealVector positionInParentA;
    private RealVector positionInParentB;
    private RealVector positionInParentC;
    RealVector positionInParent;

    private RealVector cornerRelativeToCenterA = new ArrayRealVector(new double[]{-0.5, 2.0/3.0*Math.sqrt(3.0 / 16.0)});
    private RealVector cornerRelativeToCenterB = new ArrayRealVector(new double[]{0.5, 2.0/3.0*Math.sqrt(3.0 / 16.0)});
    private RealVector cornerRelativeToCenterC = new ArrayRealVector(new double[]{0, -4.0/3.0*Math.sqrt(3.0 / 16.0)});

    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed, GameField gameField) {
        if(positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException();
        }
        this.positionInParent = new ArrayRealVector(positionInParent);
        this.movement = new ArrayRealVector(movement);
        this.currentRotation = currentRotation;
        this.rotationSpeed = rotationSpeed;
        this.gameField = gameField;
        currentColor = Color.RED;

        Log.i("game_log", "sqrt(3/16): " + Math.sqrt(3 / 16));
        update();
    }


    @Override
    public Canvas draw(Canvas canvas) {


        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(currentColor);
        paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo((float) positionInParentA.getEntry(0), (float) positionInParentA.getEntry(1));
        path.lineTo((float)positionInParentB.getEntry(0), (float)positionInParentB.getEntry(1));
        path.lineTo((float)positionInParentC.getEntry(0), (float)positionInParentC.getEntry(1));
        path.lineTo((float) positionInParentA.getEntry(0), (float) positionInParentA.getEntry(1));
        path.close();

        canvas.drawPath(path, paint);

        if (positionOfLastTouch != null) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle((int) positionOfLastTouch.getEntry(0), (int) positionOfLastTouch.getEntry(1), 10, paint);
        }
        return canvas;
    }

    private RealVector calculatePositionOfCorner(RealVector vector) {
        RealVector vectorScaled = vector.mapMultiply(200);
        RealMatrix rotationMatrix = new Array2DRowRealMatrix(new double[][]{{Math.cos(currentRotation), Math.sin(currentRotation)},
                {-Math.sin(currentRotation), Math.cos(currentRotation)}});
        RealVector vectorRotated = rotationMatrix.preMultiply(vectorScaled);
        RealVector vectorShifted = vectorRotated.add(positionInParent);
        return vectorShifted;
    }

    public void handleTap(MotionEvent event) {
        if (isInside(event.getX(), event.getY())) {
            focus();
        } else {
            unfocus();
        }
        positionOfLastTouch = new ArrayRealVector(new double[]{event.getX(), event.getY()});
//        RealVector eventCoordinates = new ArrayRealVector(new double[]{event.getX(), event.getY()});
//        RealVector movementDirection = eventCoordinates.add(positionInParent.mapMultiply(-1));
//        RealVector movementScaled = movementDirection.mapMultiply(1.0 / 100.0);
//        movement = movementScaled;
    }

    @Override
    public RealVector getPositionInParent() {
        return positionInParent;
    }

    @Override
    public RealVector getMovement() {
        return movement;
    }

    private void setMovement(RealVector movement) {
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
        positionInParent = positionInParent.add(movement);
        positionInParentA = calculatePositionOfCorner(cornerRelativeToCenterA);
        positionInParentB = calculatePositionOfCorner(cornerRelativeToCenterB);
        positionInParentC = calculatePositionOfCorner(cornerRelativeToCenterC);
    }

    @Override
    public Dockable focus() {
        isFocused = true;
        currentColor = Color.GREEN;
        gameField.setCurrentlyFocusedDockable(this);
        return this;
    }

    public void unfocus() {
        isFocused = false;
        gameField.setCurrentlyFocusedDockable(null);
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

        RealVector startPoint = new ArrayRealVector(new double[]{event1.getX(), event1.getY()});
        RealVector startPointInTriangleCoordinates = transformToTriangleCoordinates(startPoint);
        RealVector endPoint = new ArrayRealVector(new double[]{event2.getX(), event2.getY()});
        RealVector endPointInTriangleCoordinates = transformToTriangleCoordinates(endPoint);

        if (startPointInTriangleCoordinates.getNorm() < cornerRelativeToCenterA.getNorm()*200) {
            setMovement(new ArrayRealVector(new double[]{velocityX/(10.0*MainThread.MAX_FPS), velocityY/(30.0*MainThread.MAX_FPS)}));
        }else if (startPointInTriangleCoordinates.getNorm()<cornerRelativeToCenterA.getNorm()*3*200) {
            double determinantOfDirectionMatrix = startPointInTriangleCoordinates.getEntry(0) * endPointInTriangleCoordinates.getEntry(1)
                    - startPointInTriangleCoordinates.getEntry(1) * endPointInTriangleCoordinates.getEntry(0);
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
        RealVector vector = new ArrayRealVector(new double[]{x, y}).add(positionInParentA.mapMultiply(-1));
        RealVector axis1 = positionInParentB.add(positionInParentA.mapMultiply(-1));
        RealVector axis2 = positionInParentC.add(positionInParentA.mapMultiply(-1));
        double a = axis1.getEntry(0);
        double b = axis1.getEntry(1);
        double c = axis2.getEntry(0);
        double d = axis2.getEntry(1);
        RealMatrix invertedBaseMatrix = new Array2DRowRealMatrix(new double[][]{{d, -b}, {-c, a}}).scalarMultiply(1 / (a * d - b * c));
        RealVector baseCoordinates = invertedBaseMatrix.preMultiply(vector);
        Log.d(TAG, "Base coordinates: x=" + baseCoordinates.getEntry(0) + ",y=" + baseCoordinates.getEntry(1));

        return (baseCoordinates.getEntry(0) > 0 && baseCoordinates.getEntry(1) > 0 && baseCoordinates.getEntry(0) + baseCoordinates.getEntry(1) <= 1);
    }

    private RealVector transformToTriangleCoordinates(RealVector coordinates) {
        RealVector translated = coordinates.add(positionInParent.mapMultiply(-1));
        RealMatrix rotationMatrix = new Array2DRowRealMatrix(new double[][]{{Math.cos(currentRotation), -Math.sin(currentRotation)},
                {Math.sin(currentRotation), Math.cos(currentRotation)}});
        RealVector rotated = rotationMatrix.preMultiply(translated);
        return rotated;
    }
}
