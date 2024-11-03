package com.example.socinetandroid.service;

import com.example.socinetandroid.model.ApiResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostService {
    @Multipart
    @POST("posts")
    Call<ApiResponse> createPost(@Part("caption") String caption, @Part("file") MultipartBody.Part file, @Part("sharedPostId") Long sharedPostId);

    @GET("posts")
    Call<ApiResponse> getPosts();
}
