package com.example.till.game;

import android.graphics.Canvas;

import java.io.Serializable;

/**
 * Created by till on 31.12.15.
 */
public abstract class Drawer implements Serializable{
    abstract public Canvas draw(double[] transformation, Canvas canvas);
}
