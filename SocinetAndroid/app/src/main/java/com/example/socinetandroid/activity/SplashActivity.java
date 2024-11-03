package com.example.socinetandroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socinetandroid.R;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.service.AuthService;
import com.example.socinetandroid.utils.Constant;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.TokenManager;

import java.io.IOException;
import java.security.GeneralSecurityException;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init(){
        new TokenManager(this).callRefreshToken(new IRefreshTokenHandler() {
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

    private void moveToActivity(Class<?> activityClass){
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}