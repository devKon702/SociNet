package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserRepository {
    @GET("users/me")
    Call<ApiResponse> getMyInfo();

}
