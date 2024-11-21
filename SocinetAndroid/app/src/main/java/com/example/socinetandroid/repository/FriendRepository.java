package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FriendRepository {
    @GET("friend/invitation")
    Call<ApiResponse> getFriendInvitation();
    @GET("friend/{userId}")
    Call<ApiResponse> getFriendListByUser(@Path("userId") long userId);
    @POST("friend/{userId}")
    Call<ApiResponse> createFriendInvitation(@Path("userId") long id);
    @FormUrlEncoded
    @PUT("friend/{id}")
    Call<ApiResponse> responseFriendInvitation(@Path("id") long id, @Field("isAccept") boolean isAccept);
    @GET("friend/check/{userId}")
    Call<ApiResponse> checkIsFriend(@Path("userId") long userId);
}
