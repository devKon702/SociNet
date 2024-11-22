package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.request.SignUpRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthRepository {
    @POST("auth/sign-in")
    Call<ApiResponse> signIn(@Body Map<String, String> body);
    @GET("auth/refresh-token")
    Call<ApiResponse> refreshToken();
    @POST("auth/sign-up")
    Call<ApiResponse> signUp(@Body SignUpRequest request);
    @GET("auth/otp/{email}")
    Call<ApiResponse> sendOtp(@Path("email") String email);
    @GET("auth/forgot-password")
    Call<ApiResponse> searchByEmail(@Query("email") String email);
    @FormUrlEncoded
    @PUT("auth/forgot-password")
    Call<ApiResponse> forgotPassword(@Field("email") String email,
                                     @Field("newPassword") String newPassword,
                                     @Field("otp") String otp);
    @FormUrlEncoded
    @POST("auth/google")
    Call<ApiResponse> loginWithGoogle(@Field("email") String email,
                                      @Field("googleId") String googleId,
                                      @Field("name") String name,
                                      @Field("avatarUrl") String avatarUrl);
}
