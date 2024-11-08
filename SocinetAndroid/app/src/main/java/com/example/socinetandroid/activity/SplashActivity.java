package com.example.socinetandroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.utils.TokenManager;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init(){
        TokenManager tokenManager = new TokenManager(this);
        if(tokenManager.getCookieRefreshToken() == null) moveToActivity(LoginActivity.class);
        else{
            tokenManager.callRefreshToken(new IRefreshTokenHandler() {
                @Override
                public void handleSuccess() {
                    moveToActivity(MainActivity.class);
                }

                @Override
                public void handleFail() {
                    moveToActivity(LoginActivity.class);
                }
            });
        }
    }

    private void moveToActivity(Class<?> activityClass){
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}