package com.ollie.mc3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameView extends GLSurfaceView {
    private MyGLRenderer renderer;
    private float lastX, lastY;
    private boolean isMoving = false;
    private long lastTapTime = 0;

    public GameView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int screenWidth = getWidth();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                // Если нажали на левую половину экрана — идем вперед
                if (x < screenWidth / 2) {
                    isMoving = true;
                } else {
                    // Тап по правой стороне — копаем
                    long now = System.currentTimeMillis();
                    if (now - lastTapTime < 300) {
                        renderer.breakBlock();
                    }
                    lastTapTime = now;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (x >= screenWidth / 2) {
                    // Свайп справа — вращение камеры
                    float dx = x - lastX;
                    float dy = y - lastY;
                    renderer.yaw -= dx * 0.005f;
                    renderer.pitch -= dy * 0.005f;
                    if (renderer.pitch > 1.5f) renderer.pitch = 1.5f;
                    if (renderer.pitch < -1.5f) renderer.pitch = -1.5f;
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                break;
        }
        return true;
    }

    // Движение вперед (вызывается из рендера или тут)
    public void updateMovement() {
        if (isMoving) {
            float speed = 0.2f;
            float dx = (float) (Math.sin(renderer.yaw) * speed);
            float dz = (float) (-Math.cos(renderer.yaw) * speed);
            
            // Простая коллизия (не проходим сквозь блоки)
            int nx = (int)(renderer.camX + dx);
            int nz = (int)(renderer.camZ + dz);
            int ny = (int)renderer.camY;
            
            if (ny >= 0 && ny < World.HEIGHT) {
                if (nx >= 0 && nx < World.SIZE && renderer.world.blocks[nx][ny][(int)renderer.camZ] == 0) {
                    renderer.camX += dx;
                }
                if (nz >= 0 && nz < World.SIZE && renderer.world.blocks[(int)renderer.camX][ny][nz] == 0) {
                    renderer.camZ += dz;
                }
            }
        }
    }
}
