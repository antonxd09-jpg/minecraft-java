package com.ollie.mc3d;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private GameView gameView;
    private MyGLRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        FrameLayout container = findViewById(R.id.game_container);
        gameView = new GameView(this);
        renderer = gameView.getRenderer();
        container.addView(gameView);

        // Привязка кнопок
        Button btnUp = findViewById(R.id.btn_up);
        Button btnDown = findViewById(R.id.btn_down);
        Button btnLeft = findViewById(R.id.btn_left);
        Button btnRight = findViewById(R.id.btn_right);
        Button btnJump = findViewById(R.id.btn_jump);
        Button btnBreak = findViewById(R.id.btn_break);

        btnUp.setOnTouchListener((v, event) -> { renderer.moveForward = (event.getAction() == android.view.MotionEvent.ACTION_DOWN); return false; });
        btnDown.setOnTouchListener((v, event) -> { renderer.moveBackward = (event.getAction() == android.view.MotionEvent.ACTION_DOWN); return false; });
        btnLeft.setOnTouchListener((v, event) -> { renderer.moveLeft = (event.getAction() == android.view.MotionEvent.ACTION_DOWN); return false; });
        btnRight.setOnTouchListener((v, event) -> { renderer.moveRight = (event.getAction() == android.view.MotionEvent.ACTION_DOWN); return false; });

        btnJump.setOnClickListener(v -> renderer.jump());
        btnBreak.setOnClickListener(v -> renderer.breakBlock());
    }

    @Override
    protected void onResume() { super.onResume(); gameView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); gameView.onPause(); }
}
