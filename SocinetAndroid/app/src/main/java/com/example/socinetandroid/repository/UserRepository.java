package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserRepository {
    @GET("users/me")
    Call<ApiResponse> getMyInfo();

    @GET("users/{id}")
    Call<ApiResponse> getUserById(@Path("id") long id);
    @GET("users")
    Call<ApiResponse> getUserList(@Query("name") String name);

    @Multipart
    @PUT("users/me")
    Call<ApiResponse> updateUserInfo(@Part("name")RequestBody name,
                                     @Part("phone")RequestBody phone,
                                     @Part("school")RequestBody school,
                                     @Part("address")RequestBody address,
                                     @Part("isMale")RequestBody isMale,
                                     @Part MultipartBody.Part avatar);
}
