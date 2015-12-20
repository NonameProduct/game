package com.example.till.game;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by till on 20.12.15.
 */
public class MyGLSurfaceView extends GLSurfaceView{
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

}
