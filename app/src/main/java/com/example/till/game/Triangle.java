package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {
    RealVector positionInParent;
    RealVector movement;
    double currentRotation;
    double rotationSpeed;
    private RealMatrix rotationMatrix;

    private RealVector positionInParentA;
    private RealVector positionInParentB;
    private RealVector positionInParentC;

    private RealVector cornerRelativeToCenterA = new ArrayRealVector(new double[]{-0.5, 2.0/3.0*Math.sqrt(3.0 / 16.0)});
    private RealVector cornerRelativeToCenterB = new ArrayRealVector(new double[]{0.5, 2.0/3.0*Math.sqrt(3.0 / 16.0)});
    private RealVector cornerRelativeToCenterC = new ArrayRealVector(new double[]{0, -4.0/3.0*Math.sqrt(3.0 / 16.0)});

    public Triangle(double[] positionInParent, double[] movement, double currentRotation, double rotationSpeed) {
        if(positionInParent.length != 2 || movement.length != 2) {
            throw new IllegalArgumentException();
        }
        this.positionInParent = new ArrayRealVector(positionInParent);
        this.movement = new ArrayRealVector(movement);
        this.currentRotation = currentRotation;
        this.rotationSpeed = rotationSpeed;

        Log.i("game_log", "sqrt(3/16): " + Math.sqrt(3 / 16));
        update();
    }


    @Override
    public Canvas draw(Canvas canvas) {


        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(android.graphics.Color.RED);
        paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo((float)positionInParentA.getEntry(0), (float)positionInParentA.getEntry(1));
        path.lineTo((float)positionInParentB.getEntry(0), (float)positionInParentB.getEntry(1));
        path.lineTo((float)positionInParentC.getEntry(0), (float)positionInParentC.getEntry(1));
        path.lineTo((float)positionInParentA.getEntry(0), (float)positionInParentA.getEntry(1));
        path.close();

        canvas.drawPath(path, paint);
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

    public void handleActionDown(int eventX, int eventY) {
        RealVector eventCoordinates = new ArrayRealVector(new double[]{eventX, eventY});
        RealVector movementDirection = eventCoordinates.add(positionInParent.mapMultiply(-1));
        RealVector movementScaled = movementDirection.mapMultiply(1.0/100.0);
        movement = movementScaled;
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
}
