package com.example.till.game;

import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;


import com.example.till.game.gameFieldContent.Island;
import com.example.till.game.gameFieldContent.Triangle;
import com.google.gson.InstanceCreator;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private List<Island> content;
    private Island currentlyFocusedIsland = null;
    private GestureDetectorCompat gestureDetector;

    private GameField(){
        content = new ArrayList<Island>();
        Triangle t1 = new Triangle(new double[]{3, 3}, new double[]{0, 0}, Math.PI, 0);
        Triangle t2 = new Triangle(new double[]{3, 4+Triangle.HEIGHT*1.0/3.0*1.2}, new double[]{0, 0}, 0, 0);
        Triangle t3 = new Triangle(new double[]{4, 3.6}, new double[]{0, 0}, 3* Math.PI/2, 0);
        Triangle t4 = new Triangle(new double[]{2, 3.6}, new double[]{0, 0},  Math.PI/2, 0);
        content.add(t1);
        content.add(t2);
        content.add(t3);
        content.add(t4);
        drawer = new GameFieldDrawer();
    }

    public static GameField getInstance() {
        return uniqueGameField;
    }

    public List<Island> getContent() {
        return content;
    }

    public Island getCurrentlyFocusedIsland() {
        return currentlyFocusedIsland;
    }

    public void setCurrentlyFocusedIsland(Island currentlyFocusedIsland) {
        this.currentlyFocusedIsland = currentlyFocusedIsland;
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
        content = (List<Island>) ois.readObject();
        ois.close();
    }


    public boolean handleTap(double[] positionOfTap) {
        if (currentlyFocusedIsland != null) {
            currentlyFocusedIsland.unfocus();
        }
        for (Island island : content) {
            if (island.isInside(positionOfTap)) {
                island.focus();
                return false;
            }
        }
        return false;
    }


    public void update() {
        for (Island island : content) {
            island.update();
        }
        handleCollisions();
    }

    private void handleCollisions() {
        double[] identityTransformation = new double[]{0, 0, 1, 0, 0, 1};
        for (int i = 0; i < content.size(); i++) {
            for (int j = i + 1; j < content.size(); j++) {
                Island island1 = content.get(i);
                Island island2 = content.get(j);
                if (island1.dockablesCollide(identityTransformation, identityTransformation, island2)) {
                    island1.handleCollision(identityTransformation, identityTransformation, island2);
                    Log.i(TAG, "Collision detected.");
                }
            }
        }
    }

    public boolean handleFling(double[] startPoint, double[] endPoint, float velocityX, float velocityY) {
        if (currentlyFocusedIsland != null) {
            return currentlyFocusedIsland.handleFling(startPoint, endPoint, velocityX, velocityY);
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
            for (Island d : content) {
                canvas = d.getDrawer().draw(transformation, canvas);
            }
            return canvas;
        }
    }

    private class DefaultEdgeInstanceCreator implements InstanceCreator<Graph<Island, DefaultEdge>> {

        public Graph<Island, DefaultEdge> createInstance(Type type) {
            return new SimpleGraph<Island, DefaultEdge>(DefaultEdge.class);
        }
    }
}
