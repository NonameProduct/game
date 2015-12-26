package com.example.till.game;

import android.graphics.Canvas;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by till on 23.12.15.
 */
public interface Dockable {
    public Canvas draw(Canvas canvas);

    public RealVector getPositionInParent();

    public RealVector getMovement();

    public double getRotation();

    public double getRotationSpeed();

    public void update();
}
