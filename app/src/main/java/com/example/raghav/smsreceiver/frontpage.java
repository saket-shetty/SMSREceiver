package com.example.raghav.smsreceiver;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

public class frontpage extends AppCompatActivity {

    private static int splash_time=2500;
    TextView textView;
    Animation anim= new AlphaAnimation(0,1);
    AnimationSet animationSet = new AnimationSet(true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        textView =(TextView) findViewById(R.id.txtview);

        anim.setDuration(1500);
        animationSet.addAnimation(anim);
        textView.startAnimation(animationSet);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(frontpage.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },splash_time);
    }


}
