package com.example.till.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

/**
 * Created by till on 29.12.15.
 */
public class GameFieldPainter {


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
        path.moveTo((float) triangle.getPositionInParentA()[0], (float) triangle.getPositionInParentA()[1]);
        path.lineTo((float) triangle.getPositionInParentB()[0], (float) triangle.getPositionInParentB()[1]);
        path.lineTo((float) triangle.getPositionInParentC()[0], (float) triangle.getPositionInParentC()[1]);
        path.lineTo((float) triangle.getPositionInParentA()[0], (float) triangle.getPositionInParentA()[1]);
        path.close();

        canvas.drawPath(path, paint);

        if (triangle.getPositionOfLastTouch() != null) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle((int) triangle.getPositionOfLastTouch()[0], (int) triangle.getPositionOfLastTouch()[1], 10, paint);
        }
        return canvas;
    }
}
