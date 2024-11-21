package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.request.ReactionRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReactionRepository {
    @POST("reactions")
    Call<ApiResponse> createReaction(@Body ReactionRequest request);

    @DELETE("reactions/post/{id}")
    Call<ApiResponse> deleteReaction(@Path("id") long postId);
}
