package com.example.socinetandroid.service.config;

import android.content.Context;

import com.example.socinetandroid.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    private static Retrofit getClient(Context context) throws GeneralSecurityException, IOException {

        TokenManager tokenManger = new TokenManager(context);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RetrofitInterceptor(context))
                .cookieJar(new PersistentCookieJar(context))
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.101:8080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static Retrofit getInstance(){
        return retrofit;
    }

    public static Retrofit createInstance(Context context) {
        try {
            retrofit = getClient(context);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return retrofit;
    }
}
