package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;
import com.google.android.gms.common.api.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface AccountRepository {
    @GET("account")
    Call<ApiResponse> getAccount();
    @FormUrlEncoded
    @PUT("account/change-password")
    Call<ApiResponse> changePassword(@Field("oldPassword") String oldPassword,
                                     @Field("newPassword") String newString);
    @FormUrlEncoded
    @PUT("account/change-email")
    Call<ApiResponse> changeEmail(@Field("newEmail")String email,
                                  @Field("otp") String otp);
    @GET("account/login-session")
    Call<ApiResponse> getLoginSession();
    @DELETE("account/login-session")
    Call<ApiResponse> removeLoginSession(@Query("id") long id);
}
