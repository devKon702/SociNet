package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;
import com.example.socinetandroid.request.CommentRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentRepository {
    @GET("comments/post/{id}")
    Call<ApiResponse> getCommentsByPostId(@Path("id") Long id);

    @POST("comments")
    Call<ApiResponse> createComment(@Body CommentRequest commentRequest);

    @FormUrlEncoded
    @PUT("comments/{id}")
    Call<ApiResponse> updateComment(@Path("id") Long id, @Field("content") String content);

    @DELETE("comments/{id}")
    Call<ApiResponse> deleteComment(@Path("id") Long id);
}
