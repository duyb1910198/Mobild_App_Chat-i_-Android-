package com.example.nlcs_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    private void nextActivity() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if ( user == null){
                intent = new Intent( this, Activity_Login.class);
                startActivity(intent);
        } else {
            intent = new Intent( this, Activity_Home.class);
            startActivity(intent);
        }
        finish();
    }
}