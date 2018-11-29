package com.curiousca.squiz.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.curiousca.squiz.R;
import com.curiousca.squiz.RecyclerView.RecyclerViewActivity;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    Thread.sleep(2000);}catch(Exception e){}
                Intent intent = new Intent(SplashScreen.this, RecyclerViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        t.start();
    }

}
