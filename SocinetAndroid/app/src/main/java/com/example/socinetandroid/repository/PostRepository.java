package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface PostRepository {
    @GET("posts")
    Call<ApiResponse> getPosts();

    @GET("posts/user/{id}")
    Call<ApiResponse> getPostOfUser(@Path("id") long userId);

    @Multipart
    @POST("posts")
    Call<ApiResponse> createPost(@Part("caption") RequestBody caption,
                                 @Part MultipartBody.Part file,
                                 @Part("sharedPostId") RequestBody sharedPostId);

    @Multipart
    @PUT("posts/{id}")
    Call<ApiResponse> updatePost(@Path("id") long id,
                                 @Part MultipartBody.Part file,
                                 @Part("caption") RequestBody caption);

    @DELETE("posts/{id}")
    Call<ApiResponse> deletePost(@Path("id") long id);
}
