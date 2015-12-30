package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by till on 29.12.15.
 */
public class UserInterface extends GestureDetector.SimpleOnGestureListener {

    private double zoomFactor = 200;
    private double[] translation = {0, 0};

    public Canvas drawGameField(Canvas canvas) {
        List<Dockable> content = GameField.getInstance().getContent();
        for (Dockable dockable : content) {
            drawTriangle(dockable, canvas);
        }
        return canvas;
    }

    private Canvas drawTriangle(Dockable dockable, Canvas canvas) {
        Triangle triangle = (Triangle) dockable;
        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(triangle.getCurrentColor());
        paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo((float) (triangle.getPositionInParentA()[0]*200), (float) (triangle.getPositionInParentA()[1]*200));
        path.lineTo((float) (triangle.getPositionInParentB()[0]*200), (float) (triangle.getPositionInParentB()[1]*200));
        path.lineTo((float) (triangle.getPositionInParentC()[0]*200), (float) (triangle.getPositionInParentC()[1]*200));
        path.lineTo((float) (triangle.getPositionInParentA()[0]*200), (float) (triangle.getPositionInParentA()[1]*200));
        path.close();

        canvas.drawPath(path, paint);

        if (triangle.getPositionOfLastTouch() != null) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle((int) (triangle.getPositionOfLastTouch()[0]*200), (int) (triangle.getPositionOfLastTouch()[1]*200), 10, paint);
        }
        return canvas;
    }

    private double transformX(double d) {
        return (d-translation[0])/zoomFactor;
    }

    private double transformY(double d) {
        return (d - translation[1]) / zoomFactor;
    }

    private double[] transform(double[] d) {
        return new double[] {transformX(d[0]), transformY(d[1])};

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
