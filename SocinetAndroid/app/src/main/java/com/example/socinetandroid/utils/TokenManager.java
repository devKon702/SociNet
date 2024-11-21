package com.example.socinetandroid.utils;

import android.content.Context;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.socinetandroid.MyApplication;
import com.example.socinetandroid.interfaces.IRefreshTokenHandler;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Auth;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.repository.config.PersistentCookieJar;
import com.example.socinetandroid.viewmodel.AppViewModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenManager {
    private static final String TOKEN_SHARED_PREFS_FILE = Constant.TOKEN_SHARED_PREFS_FILE;
    private static final String ACCESS_TOKEN_KEY = Constant.ACCESS_TOKEN_KEY;
    private static final String REFRESH_TOKEN_KEY = Constant.REFRESH_TOKEN_KEY;

    private final EncryptedSharedPreferences sharedPreferences;
    private final Context context;

    public TokenManager(Context context) {
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            this.sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    TOKEN_SHARED_PREFS_FILE,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            this.context = context;
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAccessToken(String token) {
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, token).apply();
    }
    public void saveCookieRefreshToken(String token) {
        sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, token).apply();
    }
    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getCookieRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
    }
    public void clearSharedPref() {
        sharedPreferences.edit().clear().apply();
    }

    public void callRefreshToken(IRefreshTokenHandler iRefreshTokenHandler)  {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(context))
                .build();
        AuthRepository authRepository = new Retrofit.Builder()
                .baseUrl(Constant.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(AuthRepository.class);
        authRepository.refreshToken().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse result = response.body();
                if(result != null){
                    Auth auth = Helper.convertDataToType(result.getData(), Helper.getType(Auth.class));
                    // Save access token
                    saveAccessToken(auth.getAccessToken());
                    // Save user info
//                    GlobalData.user = auth.getAccount().getUser();
                    AppViewModel appViewModel = ((MyApplication) context.getApplicationContext()).getAppViewModel();
                    appViewModel.getLiveUser().setValue(auth.getAccount().getUser());
                    iRefreshTokenHandler.handleSuccess();
                } else {
                    iRefreshTokenHandler.handleFail();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("TOKEN", throwable.getMessage());
            }
        });

    }
}
