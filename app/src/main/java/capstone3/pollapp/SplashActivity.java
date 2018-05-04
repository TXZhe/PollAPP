package capstone3.pollapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private static int splash_time = 1000;
    private SharedPreferences.Editor editor;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences("PROFILES", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Check if already have profiles
        if(preferences.getBoolean("used",false))
        {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent nextIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(nextIntent);
                    SplashActivity.this.finish();
                }
            },splash_time);
        }
        else{
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent nextIntent = new Intent(SplashActivity.this, ProfileActivity.class);
                    startActivity(nextIntent);
                    finish();
                }
            },splash_time);
        }
    }
}
