package com.example.till.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by till on 20.12.15.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();

    private MainThread thread;
    private GameField gameField;
    private GestureDetectorCompat gestureDetector;

    public MainGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        gameField = new GameField();
        gestureDetector = new GestureDetectorCompat(context, new LocalGestureListener());
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        gameField.render(canvas);
    }

    public void update() {
        gameField.update();
    }

    public Thread getMainThread() {
        return thread;
    }

    //todo: implement action to close app when tapped on lower part of screen.
    //todo: forward other events to GameField.
    //todo: change SimpleOnGestureListener of this to below listener
    class LocalGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(TAG, "DoubleTap");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            Log.d(TAG, "DoubleTapEvent");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG, "Down");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            Log.d(TAG, "Fling");
            gameField.onFling(event1, event2, velocityX, velocityY);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(TAG, "Long press");
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            Log.d(TAG, "DoubleTapEvent");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            Log.d(TAG, "Show press");
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Log.d(TAG, "SingleTapConfirmed");
            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity) getContext()).finish();
                return true;
            } else {
                return gameField.onSingleTapConfirmed(event);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d(TAG, "SingleTapUp");
            return true;
        }
    }

}
