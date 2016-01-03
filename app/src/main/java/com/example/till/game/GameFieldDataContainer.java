package com.example.till.game;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by till on 30.12.15.
 */
public class GameFieldDataContainer implements Serializable {

    private ArrayList<Triangle> triangles = new ArrayList<>();
    private ArrayList<CompoundIsland> islands = new ArrayList<>();
    public GameFieldDataContainer(List<Dockable> list) {
        for (Dockable d : list) {
            if (Triangle.class.isInstance(d)) {
                triangles.add((Triangle) d);
            } else if (CompoundIsland.class.isInstance(d)) {
                islands.add((CompoundIsland) d);
            } else {

                throw new IllegalArgumentException("The content of the GameField contains a class for which the serialization is not implemented.");
            }
        }
    }

    public ArrayList<Dockable> getContent() {
        ArrayList<Dockable> content = new ArrayList<Dockable>();
        content.addAll(triangles);
        content.addAll(islands);
        return content;
    }

}
