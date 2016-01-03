package com.example.till.game;

import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private GameField(){
        content = new ArrayList<Dockable>();
        Triangle t1 = new Triangle(new double[]{0, 0}, new double[]{0, 0}, 0, 0);
        Triangle t2 = new Triangle(new double[]{0, 2.0/3.0*Triangle.HEIGHT}, new double[]{0, 0}, Math.PI , 0);
        Triangle t3 = new Triangle(new double[]{0.5, Triangle.HEIGHT}, new double[]{0, 0}, 0, 0);
        Triangle t4 = new Triangle(new double[]{1, 2.0/3.0*Triangle.HEIGHT}, new double[]{0, 0}, Math.PI, 0);
        Triangle t5 = new Triangle(new double[]{1, 0}, new double[]{0, 0}, 0, 0);
        Triangle t6 = new Triangle(new double[]{0.5, -1.0/3.0*Triangle.HEIGHT}, new double[]{0, 0}, Math.PI, 0);
        content.add(t1);
        content.add(t2);
        content.add(t3);
        content.add(t4);
        content.add(t5);
        content.add(t6);
//
//        t1 = new Triangle(new double[]{1, 4}, new double[]{0, 0}, 4*Math.PI, 0);
//        t2 = new Triangle(new double[]{1, 4+2*Triangle.HEIGHT/3}, new double[]{0, 0}, -11*Math.PI, 0);
//        CompoundIsland island = new CompoundIsland(t1, t2);
//        island.setMovement(new double[]{2-Triangle.HEIGHT/3, 1-Triangle.HEIGHT/3});
//        island.setRotationSpeed(Math.PI / 2);
//        island.update();
//        island.setMovement(new double[]{0, 0});
//        island.setRotationSpeed(0);
//        content.add(island);
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

    public byte[] serializeAsByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(content);
        oos.close();
        return baos.toByteArray();
    }

    public void loadSerializationFromByteArray(byte[] serialization) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialization));
        content = (List<Dockable>) ois.readObject();
        ois.close();
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
                Dockable dockable1 = content.get(i);
                Dockable dockable2 = content.get(j);
                if (dockable1.dockablesCollide(identityTransformation, identityTransformation, dockable2)) {
                    dockable1.handleCollision(identityTransformation, identityTransformation, dockable2);
                    Log.i(TAG, "Collision detected.");
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

    private class DefaultEdgeInstanceCreator implements InstanceCreator<Graph<Dockable, DefaultEdge>> {

        public Graph<Dockable, DefaultEdge> createInstance(Type type) {
            return new SimpleGraph<Dockable, DefaultEdge>(DefaultEdge.class);
        }
    }
}
