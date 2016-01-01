package com.example.till.game;

import android.view.MotionEvent;

import org.jgrapht.graph.SimpleGraph;

/**
 * Created by till on 30.12.15.
 */
public class CompoundIsland implements Dockable {
    private SimpleGraph<Triangle, Double> g;


    @Override
    public double[] getTranslation() {
        return new double[0];
    }

    @Override
    public double[] getMovement() {
        return new double[0];
    }

    @Override
    public double getCurrentRotation() {
        return 0;
    }

    @Override
    public double getRotationSpeed() {
        return 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void rollbackUpdate() {

    }

    @Override
    public Dockable focus() {
        return null;
    }

    @Override
    public void unfocus() {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean isInside(double x, double y) {
        return false;
    }

    @Override
    public void handleTap(MotionEvent event) {

    }

    @Override
    public boolean dockablesCollide(double[] transformationThis, double[] transformationDockable, Dockable dockable) {
        return false;
    }

    @Override
    public void setRotationSpeed(double v) {

    }

    @Override
    public void setMovement(double[] scale) {

    }

    @Override
    public Drawer getDrawer() {
        return null;
    }
}
