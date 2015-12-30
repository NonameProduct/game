package com.example.till.game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by till on 30.12.15.
 */
public class GameFieldDataContainer {

    private ArrayList<Triangle> triangles = new ArrayList<>();
    public GameFieldDataContainer(List<Dockable> list) {
        for (Dockable d : list) {
            if (d.getClass().getSimpleName().equals("Triangle")) {
                triangles.add((Triangle) d);
            } else {
                throw new IllegalArgumentException("The content of the GameField contains a class for which the serialization is not implemented.");
            }
        }
    }

    public ArrayList<Dockable> getContent() {
        ArrayList<Dockable> content = new ArrayList<Dockable>();
        content.addAll(triangles);
        return content;
    }

}
