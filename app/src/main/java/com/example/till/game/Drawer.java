package com.example.till.game;

import android.graphics.Canvas;

/**
 * Created by till on 31.12.15.
 */
public abstract class Drawer {
    abstract public Canvas draw(double[] transformation, Canvas canvas);
}
