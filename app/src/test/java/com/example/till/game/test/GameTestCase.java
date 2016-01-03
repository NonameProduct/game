package com.example.till.game.test;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by till on 30.12.15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class GameTestCase extends TestCase {
    public static final double[] IDENTITY_TRANSFORMATION = {0, 0, 1, 0, 0, 1};


    @Before
    public void setUp() {
        PowerMockito.mockStatic(Log.class);

    }
}
