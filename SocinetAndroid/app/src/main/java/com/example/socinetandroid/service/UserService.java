package com.example.socinetandroid.service;

import com.example.socinetandroid.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("users/me")
    Call<ApiResponse> getMyInfo();

}
