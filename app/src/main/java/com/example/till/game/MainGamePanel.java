package com.example.till.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.apache.commons.math3.linear.ArrayRealVector;

/**
 * Created by till on 20.12.15.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();

    private MainThread thread;
    private Triangle triangle;

    public MainGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        triangle = new Triangle(new double[]{400, 400}, new double[]{0, 0}, 0, Math.PI/thread.MAX_FPS);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Action: " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            triangle.handleActionDown((int) event.getX(), (int) event.getY());

            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity) getContext()).finish();
            }
        } else {
            Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
        }
        return true;
    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        triangle.draw(canvas);
    }

    public void update() {
        triangle.update();
    }
}
