package com.example.socinetandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.databinding.ActivityLoginBinding;
import com.example.socinetandroid.dialog.ForgotPasswordBottomSheet;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int GOOGLE_CODE = 1000;
    private ActivityLoginBinding bd;
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private SocketManager socketManager;
    private AppViewModel appViewModel;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


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
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut().addOnCompleteListener(task -> {});
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
                            String message = "";
                            switch(result.getMessage()){
                                case "ACCOUNT NOT FOUND":
                                    message = "Không tìm thấy tài khoản";
                                    bd.edtUsername.requestFocus();
                                    break;
                                case "INACTIVE ACCOUNT":
                                    message = "Tài khoản đang bị khóa";
                                    break;
                                case "INCORRECT PASSWORD":
                                    message = "Mật khẩu không đúng";
                                    bd.edtPassword.requestFocus();
                                    break;
                                default:
                                    message = "Đăng nhập thất bại";
                            }
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
        bd.tvForgetPassword.setOnClickListener(v -> {
            ForgotPasswordBottomSheet dialog = new ForgotPasswordBottomSheet();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        });
        bd.btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_CODE);
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String googleId = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String avatarUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;
            authRepository.loginWithGoogle(email,googleId,name, avatarUrl).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Helper.handleRetrofitResponse(response, new IRetrofitResponseHandler() {
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
                            Toast.makeText(LoginActivity.this, "Đăng nhập với Google thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                    Log.e("API", throwable.getMessage());
                }
            });
        } catch (ApiException e) {
            Log.w("GOOGLE", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}