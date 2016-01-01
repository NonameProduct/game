package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import static com.example.till.game.VectorCalculations2D.*;

import java.util.List;

/**
 * Created by till on 29.12.15.
 */
public class UserInterface extends GestureDetector.SimpleOnGestureListener {

    public double getZoomFactor() {
        return zoomFactor;
    }

    public double[] getTranslation() {
        return translation;
    }

    private double zoomFactor = 200;
    private double[] translation = {0, 0};

    public Canvas drawGameField(Canvas canvas) {
        return GameField.getInstance().getDrawer().draw(makeLinearTransformation(translation, scale(new double[]{1, 0, 0, 1}, zoomFactor)), canvas);
    }

    private double transformX(double d) {
        return (d-translation[0])/zoomFactor;
    }

    private double transformY(double d) {
        return (d - translation[1]) / zoomFactor;
    }


    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return GameField.getInstance().handleTap(transformX(event.getX()), transformY(event.getY()));
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return GameField.getInstance().handleFling(transformX(event1.getX()), transformY(event1.getY()), transformX(event2.getX()), transformY(event2.getY()),
                (float) (velocityX/zoomFactor), (float) (velocityY/zoomFactor));
    }
}
