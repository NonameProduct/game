package com.example.till.game.test;

import com.example.till.game.Dockable;
import com.example.till.game.GameField;
import com.example.till.game.Triangle;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by till on 30.12.15.
 */
public class GameFieldTest extends GameTestCase {
    public void testSaveEquivalentContainerTransformAndLoadTwoTriangles() {
        List<Dockable> content = new ArrayList<>();
        content.add(new Triangle(new double[]{400/200.0, 1200/200.0}, new double[]{34/200.0, 545/200.0}, 233, 445));
        content.add(new Triangle(new double[]{400/200.0, 400/200.0}, new double[]{349/200.0, 568/200.0}, 456, 436));
        setContentInGameField(content);

        GameField gameField = GameField.getInstance();
        String gameFieldAsJson = gameField.getGameFieldDataContainerAsJson();
        gameField.loadGameFieldCataContainerFromJson(gameFieldAsJson);

        List<Dockable> newContent = gameField.getContent();
        assertTrue(((Triangle)content.get(0)).equals(((Triangle)newContent.get(0))));
        assertTrue(((Triangle) content.get(1)).equals(((Triangle) newContent.get(1))));

        for (int i = 0; i < 100; i++) {
            gameField.update();
        }

        content = gameField.getContent();
        gameFieldAsJson = gameField.getGameFieldDataContainerAsJson();
        gameField.loadGameFieldCataContainerFromJson(gameFieldAsJson);
        newContent = gameField.getContent();
        assertTrue(((Triangle)content.get(0)).equals(((Triangle)newContent.get(0))));
        assertTrue(((Triangle)content.get(1)).equals(((Triangle)newContent.get(1))));

    }

    private void setContentInGameField(List<Dockable> content) {
        Field field = null;
        try {
            field = GameField.getInstance().getClass().getDeclaredField("content");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        field.setAccessible(true);
        try {
            field.set(GameField.getInstance(), content);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
