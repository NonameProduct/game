package com.example.till.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by till on 26.12.15.
 */
public class MainThread extends Thread {

    private static final String TAG = MainThread.class.getSimpleName();
    public static final int MAX_FPS = 50;
    private static final int MAX_FRAME_SKIPS = 5;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;

    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;
    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        long beginTime;
        long timeDiff;
        int sleepTime;
        int framesSkipped;
        long timeForUpdate;
        long timeForRender;
        long timeForLockCanvas;

        sleepTime = 0;

        while (running) {
            canvas = null;
            try{
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    canvas = this.surfaceHolder.lockCanvas();
                    timeForLockCanvas = System.currentTimeMillis() - beginTime;
                    framesSkipped = 0;
                    gamePanel.update();
                    timeForUpdate = System.currentTimeMillis() - beginTime - timeForLockCanvas;
                    this.gamePanel.render(canvas);
                    timeForRender = System.currentTimeMillis() - beginTime - timeForUpdate;
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);
                    Log.d(TAG, "Time for Lock Canvas: " + timeForLockCanvas + "; Time for Update: "
                            + timeForUpdate + "; Time For Render: " + timeForRender + "; Sleep time: " + sleepTime);

                    if (sleepTime > 0) {
                        try{
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        beginTime = System.currentTimeMillis();
                        gamePanel.update();
                        timeDiff = System.currentTimeMillis() - beginTime;
                        sleepTime += FRAME_PERIOD - timeDiff;
                        framesSkipped++;
                    }
                    if (framesSkipped > 0) {
                        Log.d(TAG, "Frames skipped: " + framesSkipped);
                    }
                    if(sleepTime > 0){
                        try{
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } ;
                    }
                }
            }finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

        }
    }
}
