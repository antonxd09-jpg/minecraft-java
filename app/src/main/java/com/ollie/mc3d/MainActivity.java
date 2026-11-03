package com.ollie.mc3d;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gameView = new GameView(this);
        setContentView(gameView);
        
        // Поток для обновления движения
        new Thread(() -> {
            while (true) {
                gameView.updateMovement();
                try { Thread.sleep(16); } catch (Exception e) {}
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.onPause();
    }
}
