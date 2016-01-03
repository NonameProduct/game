package com.example.till.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.io.Serializable;


/**
 * Center refers to the rotation center of the object.
 * Created by till on 23.12.15.
 */
public interface Dockable extends Drawable, Serializable {

    public double[] getCenterToParent();

    public double[] getMovement();

    public double getRotation();

    public double getRotationSpeed();

    public void update();

    void rollbackUpdate();

    public Dockable focus();

    public void unfocus();

    public boolean isFocused();

    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY);

    public boolean isInside(double x, double y);

    public void handleTap(MotionEvent event);

    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Dockable dockable);

    public void handleCollision(double[] transformationThis, double[] transformationDockable, Dockable dockable);

    void setRotationSpeed(double v);

    void setMovement(double[] movement);

    public void addNeighbor();

    public void removeNeighbor();

    public int getMaxNumberOfNeighbors();

    public int getNumberOfNeighbors();
}
