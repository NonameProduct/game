package com.example.till.game.test;

import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;

import com.example.till.game.CompoundIsland;
import com.example.till.game.Dockable;
import com.example.till.game.GameField;
import com.example.till.game.Triangle;

import junit.framework.TestCase;

import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.till.game.VectorCalculations2D.*;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by till on 30.12.15.
 */
public class GameFieldTest extends GameTestCase {
    GameField gameField;

    @Before
    public void testPreparation() {
        gameField = gameField.getInstance();
    }


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

    public void testFocusUnfocusedTriangle() {
//        given
        gameFieldContainsOneTriangle();
        triangleInGameFieldIsNotFocused();
        currentlyFocusedDockableIsNull();

//        when

        tapInsideTriangle();

//        then
        triangleIsFocused();

//        when
        tapOutsideTriangle();

//        then
        triangleIsUnfocused();

//        when
        tapInsideNearA();

//        then
        triangleIsFocused();

//        when
        tapOutsideNearA();

//        then
        triangleIsUnfocused();

//        when
        tapInsideNearB();

//        then
        triangleIsFocused();

//        when
        tapOutsideNearB();

//        then
        triangleIsUnfocused();

//        when
        tapInsideNearC();

//        then
        triangleIsFocused();

//        when
        tapOutsideNearC();

//        then
        triangleIsUnfocused();
    }

    private void tapOutsideNearC() {
        gameField.handleTap(1.0, 2.583123771881522);
    }

    private void tapInsideNearC() {
        gameField.handleTap(1.0, 2.5715767664977296);
    }

    private void tapOutsideNearB() {
        gameField.handleTap(0.495, 1.708438114059239);
    }

    private void tapInsideNearB() {
        gameField.handleTap(0.5049999999999999, 1.7142116167511352);
    }

    private void tapOutsideNearA() {
        gameField.handleTap(1.505, 1.708438114059239);
    }

    private void tapInsideNearA() {
        gameField.handleTap(1.4949999999999999, 1.7142116167511352);
    }

    private void triangleIsUnfocused() {
        Triangle t = (Triangle) gameField.getContent().get(0);
        assertFalse(t.isFocused());
        assertNull(gameField.getCurrentlyFocusedDockable());
    }

    private void tapOutsideTriangle() {
        gameField.handleTap(0, 0);
    }

    private void triangleIsFocused() {
        Triangle t = (Triangle) gameField.getContent().get(0);
        assertTrue(t.isFocused());
        assertTrue(gameField.getCurrentlyFocusedDockable() == t);
    }

    private void gameFieldContainsOneTriangle() {
        List<Dockable> content = new ArrayList<>();
        Triangle t = new Triangle(new double[]{1, 2}, new double[]{0, 0}, Math.PI, 0);
        content.add(t);
        Field field = null;
        try {
            field = gameField.getClass().getDeclaredField("content");
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

    private void triangleInGameFieldIsNotFocused() {
        Triangle t = (Triangle) gameField.getContent().get(0);
        assertFalse(t.isFocused());
    }

    private void currentlyFocusedDockableIsNull() {
        assertTrue(gameField.getCurrentlyFocusedDockable() == null);
    }

    private void tapInsideTriangle() {
        gameField.handleTap(1.0, 2.0);
    }

    public void testMergeTrianglesOnCollision() throws InvocationTargetException, IllegalAccessException {
//        given
        gameFieldContainsTwoCollidingTrianglesCloseEnough();

//        when
        handleCollisionsIsCalled();

//        then
        aCompoundIslandIsCreated();

    }

    public void testMergeTriangleWithCompoundIsland() throws InvocationTargetException, IllegalAccessException {
//        given
        aCompoundIslandInTheGameField();
        Triangle t = aTriangleCloseEnoughForMergeAndBeforeIslandInGameFieldContent();

//        when
        handleCollisionsIsCalled();

//        then
        itGetsAddedToTheCompoundIsland(t);

//        given
        aCompoundIslandInTheGameField();
        t = aTriangleCloseEnoughForMergeAndAfterIslandInGameFieldContent();

//        when
        handleCollisionsIsCalled();

//        then
        itGetsAddedToTheCompoundIsland(t);
    }

    private Triangle aTriangleCloseEnoughForMergeAndBeforeIslandInGameFieldContent() {
        Triangle t = aTriangleCloseEnoughForMergeAndAfterIslandInGameFieldContent();
        List<Dockable> content = gameField.getContent();
        Dockable dockable = content.remove(1);
        content.add(0, dockable);
        assertTrue(content.get(0).getClass().getSimpleName().equals(Triangle.class.getSimpleName())
                && content.get(1).getClass().getSimpleName().equals(CompoundIsland.class.getSimpleName()));
        return t;
    }

    private Triangle aTriangleCloseEnoughForMergeAndAfterIslandInGameFieldContent() {
        Triangle t = new Triangle(new double[]{4-0.5*0.9, 7-Triangle.A[1]}, new double[]{0, 0}, Math.PI, 0);
        gameField.getContent().add(t);

        double[] identity = {0, 0, 1, 0, 0, 1};
        assertTrue(t.dockablesCollide(identity, identity, gameField.getContent().get(0)));
        return t;
    }

    private void aCompoundIslandInTheGameField() throws InvocationTargetException, IllegalAccessException {
        gameFieldContainsTwoCollidingTrianglesCloseEnough();
        handleCollisionsIsCalled();
    }

    private void itGetsAddedToTheCompoundIsland(Triangle t) {
        assertEquals(1, gameField.getContent().size());
        List<Dockable> content = gameField.getContent();
        CompoundIsland island = (CompoundIsland) content.get(0);
        Set<Dockable> contentIslandSet = island.getContent();
        List<Dockable> contentIsland = new ArrayList<>(contentIslandSet);
        assertTrue(contentIsland.size() == 3);
        assertTrue(island.contains(t));
        assertTrue(normL2(substract(contentIsland.get(0).getTranslation(), t.getTranslation())) <= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING
                ||normL2(substract(contentIsland.get(1).getTranslation(), t.getTranslation())) <= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING);
        assertTrue(normL2(substract(contentIsland.get(0).getTranslation(), t.getTranslation())) >= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING/4
                ||normL2(substract(contentIsland.get(1).getTranslation(), t.getTranslation())) >= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING/4);
    }


    private void aCompoundIslandIsCreated() {
        assertEquals(1, gameField.getContent().size());
        assertTrue(CompoundIsland.class.getSimpleName().equals(gameField.getContent().get(0).getClass().getSimpleName()));
        Set<Dockable> contentSet = ((CompoundIsland) gameField.getContent().get(0)).getContent();
        List<Dockable> content = new ArrayList<>(contentSet);
        assertTrue(normL2(substract(content.get(0).getTranslation(), content.get(1).getTranslation()))<= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING);
        assertTrue(normL2(substract(content.get(0).getTranslation(), content.get(1).getTranslation())) >= CompoundIsland.MAX_DISTANCE_TO_TRIGGER_DOCKING / 4);
    }

    private void handleCollisionsIsCalled() throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            method = gameField.getClass().getDeclaredMethod("handleCollisions");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        method.invoke(gameField);
    }

    private void gameFieldContainsTwoCollidingTrianglesCloseEnough() {
        Triangle t1 = new Triangle(new double[]{4, 7}, new double[]{0, 0}, 0, 0);
        Triangle t2 = new Triangle(new double[]{4, 7+2*Triangle.A[1]*0.9}, new double[]{0, 0}, Math.PI, 0);
        List<Dockable> content = new ArrayList<>();
        content.add(t1);
        content.add(t2);

        double[] identity = {0, 0, 1, 0, 0, 1};
        assertTrue(t1.dockablesCollide(identity, identity, t2));
        setContentInGameField(content);

    }

    public void testRepellTrianglesOnCollision() throws InvocationTargetException, IllegalAccessException {
//        given
        gameFieldContainsTwoCollidingTrianglesNotCloseEnough();

//        when
        handleCollisionsIsCalled();

//        then
        theTrianglesRepell();

    }

    private void theTrianglesRepell() {
        List<Dockable> content = gameField.getContent();
        assertEquals(2, content.size());
        assertTrue(content.get(0).getClass().getSimpleName().equals(Triangle.class.getSimpleName()));
        assertTrue(content.get(1).getClass().getSimpleName().equals(Triangle.class.getSimpleName()));
    }

    private void gameFieldContainsTwoCollidingTrianglesNotCloseEnough() {
        Triangle t1 = new Triangle(new double[]{5, 2}, new double[]{0, 0}, 0, 0);
        Triangle t2 = new Triangle(new double[]{5+0.75*0.9, 2-(Triangle.A[1] - Triangle.C[1])/2.0}, new double[]{0, 0}, 0, 0);
        List<Dockable> content = new ArrayList<>();
        content.add(t1);
        content.add(t2);
        setContentInGameField(content);

        double[] identity = {0, 0, 1, 0, 0, 1};
        assertTrue(t1.dockablesCollide(identity, identity, t2));

    }

}
