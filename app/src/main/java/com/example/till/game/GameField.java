package com.example.till.game;

import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by till on 26.12.15.
 */
public class GameField implements Drawable{
    private static GameField uniqueGameField = new GameField();
    private final String TAG = GameField.class.getSimpleName();
    private GameFieldDrawer drawer;
    private List<Dockable> content;
    private Dockable currentlyFocusedDockable = null;
    private GestureDetectorCompat gestureDetector;

    private GameField() {
        content = new ArrayList<Dockable>();
        content.add(new Triangle(new double[]{2, 2}, new double[]{0, 0}, 0, 0));
        content.add(new Triangle(new double[]{2, 6}, new double[]{0, 0}, Math.PI, 0));
        drawer = new GameFieldDrawer();
    }

    public static GameField getInstance() {
        return uniqueGameField;
    }

    public List<Dockable> getContent() {
        return content;
    }

    public Dockable getCurrentlyFocusedDockable() {
        return currentlyFocusedDockable;
    }

    public void setCurrentlyFocusedDockable(Dockable currentlyFocusedDockable) {
        this.currentlyFocusedDockable = currentlyFocusedDockable;
    }

    public String getGameFieldDataContainerAsJson() {
        Gson gson = new Gson();
        GameFieldDataContainer gameFieldDataContainer = new GameFieldDataContainer(content);
        return gson.toJson(gameFieldDataContainer);
    }

    public void loadGameFieldCataContainerFromJson(String jsonGameFieldDataContainer) {
        Gson gson = new Gson();
        GameFieldDataContainer gameFieldDataContainer = gson.fromJson(jsonGameFieldDataContainer, GameFieldDataContainer.class);
        content = gameFieldDataContainer.getContent();
    }


    public boolean handleTap(double x, double y) {
        if (currentlyFocusedDockable != null) {
            currentlyFocusedDockable.unfocus();
        }
        for (Dockable dockable : content) {
            if (dockable.isInside(x, y)) {
                dockable.focus();
                return false;
            }
        }
        return false;
    }


    public void update() {
        for (Dockable dockable : content) {
            dockable.update();
        }
        handleCollisions();
    }

    private void handleCollisions() {
        double[] identityTransformation = new double[]{0, 0, 1, 0, 0, 1};
        for (int i = 0; i < content.size(); i++) {
            for (int j = i + 1; j < content.size(); j++) {
                Triangle triangle1 = (Triangle) content.get(i);
                Triangle triangle2 = (Triangle) content.get(j);
                if (triangle1.trianglesCollide(identityTransformation, identityTransformation, triangle2)) {
                    Log.i(TAG, "Collision detected.");
                    triangle1.rollbackUpdate();
                    triangle2.rollbackUpdate();
                    triangle1.setRotationSpeed(-triangle1.getRotationSpeed());
                    triangle1.setMovement(VectorCalculations2D.scale(triangle1.getMovement(), -1));
                    triangle2.setRotationSpeed(-triangle2.getRotationSpeed());
                    triangle2.setMovement(VectorCalculations2D.scale(triangle2.getMovement(), -1));

                }
            }
        }
    }

    public boolean handleFling(double event1x, double event1y, double event2x, double event2y, float velocityX, float velocityY) {
        if (currentlyFocusedDockable != null) {
            return currentlyFocusedDockable.handleFling(event1x, event1y, event2x, event2y, velocityX, velocityY);
        } else {
            return false;
        }
    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }

    private class GameFieldDrawer extends Drawer{
        @Override
        public Canvas draw(double[] transformation, Canvas canvas) {
            for (Dockable d : content) {
                canvas = d.getDrawer().draw(transformation, canvas);
            }
            return canvas;
        }
    }
}
