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

import static com.example.till.game.VectorCalculations2D.angleBetweenVectors;
import static com.example.till.game.VectorCalculations2D.calculateRotationMatrix;
import static com.example.till.game.VectorCalculations2D.concatLinearTransformation;
import static com.example.till.game.VectorCalculations2D.invertLinearTransformation;
import static com.example.till.game.VectorCalculations2D.makeLinearTransformation;
import static com.example.till.game.VectorCalculations2D.normL2;
import static com.example.till.game.VectorCalculations2D.scale;
import static com.example.till.game.VectorCalculations2D.substract;
import static com.example.till.game.VectorCalculations2D.transformLinear;

/**
 * Created by till on 20.12.15.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();

    private MainThread thread;
    private GestureDetectorCompat gestureDetector;
    private double[] coordsOfLastActionMoveEventFinger0 = {-1, -1};
    private double[] coordsOfLastActionMoveEventFinger1 = {-1, -1};
    private double[] trafoGameFieldToMainGamePanel;

    private double zoomFactor = 200;
    private double[] translation = {0, 0};

    public MainGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        gestureDetector = new GestureDetectorCompat(context, new LocalGestureListener());
        trafoGameFieldToMainGamePanel = new double[]{translation[0], translation[1], zoomFactor, 0, 0, zoomFactor};
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
        Log.d(TAG, "action masked: " + event.getActionMasked() + ", pointer count: " + event.getPointerCount());
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE && event.getPointerCount() > 1) {
            if (coordsOfLastActionMoveEventFinger0[0] != -1) {
                shiftView(event);

            }
            coordsOfLastActionMoveEventFinger0[0] = event.getX(0);
            coordsOfLastActionMoveEventFinger0[1] = event.getY(0);
            coordsOfLastActionMoveEventFinger1[0] = event.getX(1);
            coordsOfLastActionMoveEventFinger1[1] = event.getY(1);
        }
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            coordsOfLastActionMoveEventFinger0[0] = coordsOfLastActionMoveEventFinger0[1] = coordsOfLastActionMoveEventFinger1[0] = coordsOfLastActionMoveEventFinger1[1] = -1;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void shiftView(MotionEvent event) {
        double[] newPosition0 = new double[]{event.getX(0), event.getY(0)};
        double[] newPosition1 = new double[]{event.getX(1), event.getY(1)};
        double[] oldPosition0 = coordsOfLastActionMoveEventFinger0;
        double[] oldPosition1 = coordsOfLastActionMoveEventFinger1;
        double[] shiftOldPos0ToCoordinateBase = {-oldPosition0[0], -oldPosition0[1], 1, 0, 0, 1};
        double angleOld1MinusOld0_New1MinusNew0 = angleBetweenVectors(substract(oldPosition1, oldPosition0), substract(newPosition1, newPosition0));
        double[] rotationAndShiftToNew0 = makeLinearTransformation(newPosition0, scale(calculateRotationMatrix(angleOld1MinusOld0_New1MinusNew0), normL2(substract(newPosition1, newPosition0)) / normL2(substract(oldPosition1, oldPosition0))));
        double[] trafoOldPositionsToNewPositions = concatLinearTransformation(rotationAndShiftToNew0, shiftOldPos0ToCoordinateBase);
        trafoGameFieldToMainGamePanel = concatLinearTransformation(trafoOldPositionsToNewPositions, trafoGameFieldToMainGamePanel);
    }

    protected void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        GameField.getInstance().getDrawer().draw(trafoGameFieldToMainGamePanel, canvas);
    }

    public void update() {
        GameField.getInstance().update();
    }

    public MainThread getMainThread() {
        return thread;
    }

    //todo: implement action to close app when tapped on lower part of screen.
    //todo: forward other events to GameField.
    //todo: change SimpleOnGestureListener of this to below listener
    class LocalGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            Log.d(TAG, "Double tap event");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            double[] startPointInGamePanel = new double[]{event1.getX(), event1.getY()};
            double[] endPointInGamePanel = new double[]{event2.getX(), event2.getY()};
            double[] trafoMainGamePanelToGameField = invertLinearTransformation(trafoGameFieldToMainGamePanel);
            return GameField.getInstance().handleFling(transformLinear(trafoMainGamePanelToGameField, startPointInGamePanel), transformLinear(trafoMainGamePanelToGameField, endPointInGamePanel),
                    (float) (velocityX / zoomFactor), (float) (velocityY / zoomFactor));
        }

        @Override
        public void onLongPress(MotionEvent event) {

        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity) getContext()).finish();
                return true;
            } else {
                double[] positionOfTap = new double[]{event.getX(), event.getY()};
                double[] trafoMainGamePanelToGameField = invertLinearTransformation(trafoGameFieldToMainGamePanel);
                return GameField.getInstance().handleTap(transformLinear(trafoMainGamePanelToGameField, positionOfTap));
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }


}
