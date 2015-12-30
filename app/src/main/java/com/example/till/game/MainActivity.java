package com.example.till.game;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private MainGamePanel mainGamePanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainGamePanel = new MainGamePanel(this);
        setContentView(mainGamePanel);
    }


    @Override
    protected  void onDestroy() {
        Log.d(TAG, "Destroying...");
        mainGamePanel.getMainThread().setRunning(false);
        super.onDestroy();
    }

    @Override
    protected  void onStop() {
        Log.d(TAG, "Stopping...");
        super.onStop();
    }

    @Override
    protected void onPause() {
        mainGamePanel.getMainThread().setRunning(false);
        super.onPause();
    }


}
