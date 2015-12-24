package com.example.till.game;

import android.graphics.Canvas;

/**
 * Created by till on 23.12.15.
 */
public interface Dockable {
    public Canvas drawMe(Canvas canvas);

    public MyVector getPositionInParent();

    public MyVector getMovement();

    public double getRotation();

    public double getRotationSpeed();

    public void update();
}
