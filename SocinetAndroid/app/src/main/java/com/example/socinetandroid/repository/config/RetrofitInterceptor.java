package com.example.socinetandroid.repository.config;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.socinetandroid.activity.LoginActivity;
import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.model.Auth;
import com.example.socinetandroid.repository.AuthRepository;
import com.example.socinetandroid.utils.Constant;
import com.example.socinetandroid.utils.Helper;
import com.example.socinetandroid.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInterceptor implements Interceptor {
    private final Context context;

    public RetrofitInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        TokenManager tokenManager = new TokenManager(context);
        String accessToken = tokenManager.getAccessToken();
        if(accessToken != null){
            request = request
                    .newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        }

        Response response = chain.proceed(request);
        if(response.code() == 401){
            response.close();
            // Call to refresh access token
            OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(context))
                        .build();

            AuthRepository authRepository = new Retrofit
                    .Builder()
                    .baseUrl(Constant.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                    .create(AuthRepository.class);
            ApiResponse result = authRepository.refreshToken().execute().body();
            if(result != null){
                Auth auth = Helper.convertDataToType(result.getData(), Helper.getType(Auth.class));

                tokenManager.saveAccessToken(auth.getAccessToken());
                request = chain.request()
                        .newBuilder()
                        .header("Authorization", "Bearer " + auth.getAccessToken())
                        .build();
                response = chain.proceed(request);
            } else {
                logout();
            }
        }
        return response;
    }

    private void logout(){
        new TokenManager(context).clearSharedPref();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
