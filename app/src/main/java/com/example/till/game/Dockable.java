package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;


/**
 * Created by till on 23.12.15.
 */
public interface Dockable {

    public double[] getPositionInParent();

    public double[] getMovement();

    public double getCurrentRotation();

    public double getRotationSpeed();

    public void update();

    public Dockable focus();

    public void unfocus();

    public boolean isFocused();

    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY);

    public boolean isInside(double x, double y);

    public void handleTap(MotionEvent event);
}
