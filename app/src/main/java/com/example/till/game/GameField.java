package com.example.till.game;

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
public class GameField extends GestureDetector.SimpleOnGestureListener {
    private static GameField uniqueGameField = new GameField();
    private final String TAG = GameField.class.getSimpleName();

    public List<Dockable> getContent() {
        return content;
    }

    private List<Dockable> content;

    public void setCurrentlyFocusedDockable(Dockable currentlyFocusedDockable) {
        this.currentlyFocusedDockable = currentlyFocusedDockable;
    }

    private Dockable currentlyFocusedDockable = null;
    private GestureDetectorCompat gestureDetector;

    private GameField() {
        content = new ArrayList<Dockable>();
        content.add(new Triangle(new double[]{400, 400}, new double[]{0, 0}, 0, 0));
        content.add(new Triangle(new double[]{400, 1200}, new double[]{0, 0}, Math.PI, 0));
    }

    public static GameField getInstance() {
        return uniqueGameField;
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

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    //todo: implement handling of action events. Maybe extension of SimpleOnGestureListener is not necessary for that?
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(TAG, "Action: " + event.getAction());
        if (currentlyFocusedDockable != null) {
            currentlyFocusedDockable.unfocus();
        }
        for (Dockable dockable : content) {
            if (dockable.isInside(event.getX(), event.getY())) {
                dockable.focus();
                return false;
            }
        }
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


    public void update() {
        for (Dockable dockable : content) {
            dockable.update();
        }
        handleCollisions();
    }

    private void handleCollisions() {
        for (int i = 0; i < content.size(); i++) {
            for (int j = i + 1; j < content.size(); j++) {
                Triangle triangle1 = (Triangle) content.get(i);
                Triangle triangle2 = (Triangle) content.get(j);
                if (triangle1.trianglesCollide(triangle2)) {
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


    class JsonTestClass {
        public int x = 1;
        public boolean b = true;
    }
}
