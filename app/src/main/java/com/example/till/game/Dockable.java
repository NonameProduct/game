package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by till on 23.12.15.
 */
public interface Dockable {
    public Canvas draw(Canvas canvas);

    public RealVector getPositionInParent();

    public RealVector getMovement();

    public double getCurrentRotation();

    public double getRotationSpeed();

    public void update();

    public Dockable focus();

    public void unfocus();

    public boolean isFocused();

    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY);
}
