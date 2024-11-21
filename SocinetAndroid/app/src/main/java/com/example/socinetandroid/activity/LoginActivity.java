package com.example.socinetandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.databinding.ActivityLoginBinding;
import com.example.socinetandroid.interfaces.IRetrofitResponseHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Auth;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.repository.UserRepository;
import com.example.socinetandroid.repository.config.RetrofitClient;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.SocketManager;
import com.example.socinetandroid.utils.TokenManager;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding bd;
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private SocketManager socketManager;
    private AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
        setContentView(bd.getRoot());
        setEvent();
    }

    private void initial(){
        appViewModel = ((MyApplication) getApplication()).getAppViewModel();
        socketManager = SocketManager.getInstance(getApplication());
        socketManager.disconnect();
        bd = ActivityLoginBinding.inflate(getLayoutInflater());
        authRepository = RetrofitClient.createInstance(this).create(AuthRepository.class);
        tokenManager = new TokenManager(this);
    }

    private void setEvent() {
        bd.btnLogin.setOnClickListener(v -> {
            String username = bd.edtUsername.getText().toString();
            String password = bd.edtPassword.getText().toString();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            authRepository.signIn(requestBody).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler(){
                        @Override
                        public void onSuccess(ApiResponse result) {
                            Auth auth = Helper.convertDataToType(result.getData(), Helper.getType(Auth.class));
                            appViewModel.setUser(auth.getAccount().getUser());
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
        UserRepository userRepository = RetrofitClient.createInstance(this).create(UserRepository.class);
        userRepository.getMyInfo().enqueue(new Callback<ApiResponse>() {
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