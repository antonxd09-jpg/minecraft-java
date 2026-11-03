package com.ollie.mc3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameView extends GLSurfaceView {
    private MyGLRenderer renderer;
    private float lastX, lastY;

    public GameView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);
    }

    public MyGLRenderer getRenderer() {
        return renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // Вращение камеры свайпом по экрану
                float dx = x - lastX;
                float dy = y - lastY;
                renderer.yaw -= dx * 0.005f;
                renderer.pitch -= dy * 0.005f;
                if (renderer.pitch > 1.5f) renderer.pitch = 1.5f;
                if (renderer.pitch < -1.5f) renderer.pitch = -1.5f;
                lastX = x;
                lastY = y;
                break;
        }
        return true;
    }
}
