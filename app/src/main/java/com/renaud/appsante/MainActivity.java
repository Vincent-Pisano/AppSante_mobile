package com.renaud.appsante;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final Integer SPLASH_SCREEN = 3000;

    Animation topAnimation, bottomAnimation;
    ImageView image;
    TextView title, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        topAnimation.setFillAfter(false);
        bottomAnimation.setFillAfter(false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        image = findViewById(R.id.imageHome);
        title = findViewById(R.id.titleHome);
        slogan = findViewById(R.id.sloganHome);

        image.startAnimation(topAnimation);
        title.startAnimation(bottomAnimation);
        slogan.startAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(title, "logo_name");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
                startActivity(intent);
            }
        }, SPLASH_SCREEN);
    }
}