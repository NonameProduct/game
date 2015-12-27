package com.example.till.game;

import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by till on 26.12.15.
 */
public class GameField extends GestureDetector.SimpleOnGestureListener {
    private final String TAG = GameField.class.getSimpleName();
    private Triangle triangle;

    public void setCurrentlyFocusedDockable(Dockable currentlyFocusedDockable) {
        this.currentlyFocusedDockable = currentlyFocusedDockable;
    }

    private Dockable currentlyFocusedDockable = null;
    private GestureDetectorCompat gestureDetector;

    public GameField() {
        triangle = new Triangle(new double[]{400, 1200}, new double[]{0, 0}, 0, 0, this);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    //todo: implement handling of action events. Maybe extension of SimpleOnGestureListener is not necessary for that?
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(TAG, "Action: " + event.getAction());
        triangle.handleTap(event);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (currentlyFocusedDockable != null) {
            return currentlyFocusedDockable.onFling(event1, event2, velocityX, velocityY);
        } else {
            return false;
        }
    }

    public void render(Canvas canvas) {
        triangle.draw(canvas);
    }

    public void update() {
        triangle.update();
    }


}
