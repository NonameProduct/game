package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by till on 23.12.15.
 */
public class GameField implements Runnable {
    Triangle triangle;
    private SurfaceView surfaceView;
    private int updatesPerSecond;
    private final String logTag = "game_log";
    private long cycleLength;


    public GameField(MySurfaceView mySurfaceView, int updatesPerSecond) {
        this.surfaceView = mySurfaceView;
        this.updatesPerSecond = updatesPerSecond;
        triangle = new Triangle(new MyVector(0, 0), new MyVector(200, 200).scale(1.0/updatesPerSecond), 0, 0);
        cycleLength = 1000000000/updatesPerSecond;
    }

    public Canvas drawMe(Canvas canvas) {
        return triangle.drawMe(canvas);
    }

    @Override
    public void run() {
        long time;
        long remainder;
        for (int i = 0; i < 5*updatesPerSecond; i++) {
            time = System.nanoTime();
            updateState();
            Log.i(logTag, "updateState Zeit: " + ((time - System.nanoTime()) / 1000000));
            updateDisplay();
            Log.i(logTag, "updateDisplay Zeit: " + ((time - System.nanoTime()) / 1000000));
            long spentTime = (System.nanoTime() - time);
            remainder = cycleLength - spentTime;
            Log.i(logTag, "Remaining time: " + (remainder/1000000));
            try {
                Thread.currentThread().sleep(remainder/1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateState() {
        triangle.update();
    }

    private void updateDisplay() {
        SurfaceHolder sh = surfaceView.getHolder();
        Canvas canvas = sh.lockCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);

        canvas = this.drawMe(canvas);

        sh.unlockCanvasAndPost(canvas);

    }

    public int getUpdatesPerSecond() {
        return updatesPerSecond;
    }
}
