package com.example.smartbuildingsystem;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.Timer;
import java.util.TimerTask;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity {
    private ImageView bgapp, clover;
    private LinearLayout textsplash, texthome, menus;
    private Animation frombottom;
    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize all objects
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        bgapp = (ImageView) findViewById(R.id.bgapp);
        clover = (ImageView) findViewById(R.id.clover);
        textsplash = (LinearLayout) findViewById(R.id.textsplash);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        menus = (LinearLayout) findViewById(R.id.menus);
        gifImageView = (GifImageView)findViewById(R.id.gif_lightbulb);

        // start animations , add delay etc
        bgapp.animate().translationY(-2400).setDuration(2800).setStartDelay(300);
        clover.animate().alpha(0).setDuration(2800).setStartDelay(300);
        textsplash.animate().translationY(140).alpha(0).setDuration(2800).setStartDelay(300);
        texthome.startAnimation(frombottom);
        menus.startAnimation(frombottom);
        gifImageView.setVisibility(View.VISIBLE);

        // add delay on bulb  gif
        gifImageView.animate().setStartDelay(3000);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer t = new Timer();
        // add timer delay in milli seconds
        t.schedule(task, 5500);
    }
}