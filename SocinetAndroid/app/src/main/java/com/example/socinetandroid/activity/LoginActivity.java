package com.example.socinetandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socinetandroid.databinding.ActivityLoginBinding;
import com.example.socinetandroid.databinding.ActivityMainBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Auth;
import com.example.socinetandroid.service.AuthService;
import com.example.socinetandroid.service.PostService;
import com.example.socinetandroid.service.UserService;
import com.example.socinetandroid.service.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.TokenManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding bd;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
        setContentView(bd.getRoot());
        setEvent();
    }

    private void initial(){
        bd = ActivityLoginBinding.inflate(getLayoutInflater());
        authService = RetrofitClient.createInstance(this).create(AuthService.class);
        tokenManager = new TokenManager(this);

    }

    private void setEvent() {
        bd.btnLogin.setOnClickListener(v -> {
            String username = bd.edtUsername.getText().toString();
            String password = bd.edtPassword.getText().toString();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            authService.signIn(requestBody).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler(){
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Auth auth = Helper.convertDataToType(result.getData(), Helper.getType(Auth.class));
                            tokenManager.saveAccessToken(auth.getAccessToken());

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        @Override
                        public void onFail(ApiResponse result) {
                            bd.tv.setText(result.toString());
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("AUTH", throwable.getMessage());
                }
            });

        });
        bd.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void checkLogged() throws GeneralSecurityException, IOException {
        UserService userService = RetrofitClient.createInstance(this).create(UserService.class);
        userService.getMyInfo().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
                    @Override
                    public void onSuccess(ApiResponse result) {
                        if(result.isSuccess()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFail(ApiResponse result) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {

            }
        });
    }
}