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
//        content = new ArrayList<Dockable>();
//        Gson gson = new Gson();
//        content.add(new Triangle(new double[]{400, 1200}, new double[]{0, 0}, 0, 0));
//        content.add(new Triangle(new double[]{400, 300}, new double[]{0, 0}, 0, 0));
//        content.add(new Triangle(new double[]{1000, 1200}, new double[]{0, 0}, 0, 0));
//        content.add(new Triangle(new double[]{1000, 300}, new double[]{0, 0}, 0, 0));
//        Log.d(TAG, getContentAsJson());
//        Log.d(TAG, "bla");
        loadContentFromJson(GameFieldConfigurations.fourTriangles);
    }

    public static GameField getInstance() {
        return uniqueGameField;
    }

    public String getContentAsJson() {
        Gson gson = new Gson();
        List<String> strings = new ArrayList<String>();
        for (Dockable d : content) {
            strings.add(gson.toJson(d));
        }
        return gson.toJson(strings);
    }

    public void loadContentFromJson(String jsonStringList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> stringsList = gson.fromJson(jsonStringList, type);
        List<Dockable> result = new ArrayList<Dockable>();
        for (String s : stringsList) {
            result.add(gson.fromJson(s, Triangle.class));
        }
        content = result;
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
                if (triangle2.isInside(triangle1.getPositionInParentA()) || triangle2.isInside(triangle1.getPositionInParentB()) || triangle2.isInside(triangle1.getPositionInParentC()) ||
                        triangle1.isInside(triangle2.getPositionInParentA()) || triangle1.isInside(triangle2.getPositionInParentB()) || triangle1.isInside(triangle2.getPositionInParentC())) {
                    Log.i(TAG, "Collision detected.");
                    triangle1.setRotationSpeed(0);
                    triangle1.setMovement(new double[]{0.0, 0.0});
                    triangle2.setRotationSpeed(0);
                    triangle2.setMovement(new double[]{0.0, 0.0});

                }
            }
        }
    }


    class JsonTestClass {
        public int x = 1;
        public boolean b = true;
    }
}
