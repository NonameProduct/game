package com.example.till.game.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.till.game.Triangle;

import junit.framework.TestCase;

/**
 * Created by till on 27.12.15.
 */
public class TriangleTest extends TestCase {

    @SmallTest
    public void testTriangle() {
        int i = 1;
        int j = 2;
        assertEquals(i, j);
    }
}
