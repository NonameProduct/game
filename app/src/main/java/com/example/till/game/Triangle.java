package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {
    RealVector positionInParent;
    RealVector movement;
    double rotation;
    double rotationSpeed;


    private RealVector a = new ArrayRealVector(new double[]{-0.5, Math.sqrt(3.0 / 16.0)});
    private RealVector b = new ArrayRealVector(new double[]{0.5, Math.sqrt(3.0 / 16.0)});
    private RealVector c = new ArrayRealVector(new double[]{0, -Math.sqrt(3.0 / 16.0)});

    public Triangle(RealVector positionInParent, RealVector movement, double rotation, double rotationSpeed) {
        this.positionInParent = positionInParent;
        this.movement = movement;
        this.rotation = rotation;
        this.rotationSpeed = rotationSpeed;

        Log.i("game_log", "sqrt(3/16): " + Math.sqrt(3 / 16));
        a = calculatePoint(a);
        b = calculatePoint(b);
        c = calculatePoint(c);
    }


    @Override
    public Canvas drawMe(Canvas canvas) {


        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(android.graphics.Color.RED);
        paint.setAntiAlias(true);

        Point pointA = new Point((int) a.getEntry(0), (int) a.getEntry(1));
        Point pointB = new Point((int) b.getEntry(0), (int) b.getEntry(1));
        Point pointC = new Point((int) c.getEntry(0), (int) c.getEntry(1));

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(pointA.x, pointA.y);
        path.lineTo(pointB.x, pointB.y);
        path.lineTo(pointC.x, pointC.y);
        path.lineTo(pointA.x, pointA.y);
        path.close();

        canvas.drawPath(path, paint);
        return canvas;
    }

    private RealVector calculatePoint(RealVector vector) {
        vector = vector.mapMultiply(100);
        vector = vector.add(positionInParent);
        return vector;
    }

    @Override
    public RealVector getPositionInParent() {
        return positionInParent;
    }

    @Override
    public RealVector getMovement() {
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

    public void update() {
        a = a.add(movement);
        b = b.add(movement);
        c = c.add(movement);
        Log.i("game_log", "a = " + a + ", movement: " + movement);
    }
}
