package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

/**
 * Created by till on 23.12.15.
 */
public class Triangle implements Dockable {
    MyVector positionInParent;
    MyVector movement;
    double rotation;
    double rotationSpeed;


    private MyVector a = new MyVector(-0.5, Math.sqrt(3.0 / 16.0));
    private MyVector b = new MyVector(0.5, Math.sqrt(3.0 / 16.0));
    private MyVector c = new MyVector(0, -Math.sqrt(3.0 / 16.0));

    public Triangle(MyVector positionInParent, MyVector movement, double rotation, double rotationSpeed) {
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

        Point pointA = new Point((int) a.getX(), (int) a.getY());
        Point pointB = new Point((int) b.getX(), (int) b.getY());
        Point pointC = new Point((int) c.getX(), (int) c.getY());

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

    private MyVector calculatePoint(MyVector vector) {
        vector = vector.scale(100);
        vector = vector.add(positionInParent);
        return vector;
    }

    @Override
    public MyVector getPositionInParent() {
        return positionInParent;
    }

    @Override
    public MyVector getMovement() {
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
