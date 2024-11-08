package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthRepository {
    @POST("auth/sign-in")
    Call<ApiResponse> signIn(@Body Map<String, String> body);

    @GET("auth/refresh-token")
    Call<ApiResponse> refreshToken();

}
