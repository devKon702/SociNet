package com.example.socinetandroid.repository;
import com.example.socinetandroid.model.ApiResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomRepository {
    @GET("rooms")
    Call<ApiResponse> getRoomOfMe();
    @GET("rooms/{id}")
    Call<ApiResponse> getRoomById(@Path("id") long roomId);
    @Multipart
    @POST("rooms")
    Call<ApiResponse> createRoom(@Part("name")RequestBody name,
                                 @Part MultipartBody.Part avatarFile);

    @Multipart
    @PUT("rooms/{id}")
    Call<ApiResponse> updateRoom(@Path("id") long roomId,
                                 @Part("name")RequestBody name,
                                 @Part MultipartBody.Part avatarFile);

    @DELETE("room/{id}")
    Call<ApiResponse> deleteRoom(@Path("id") long roomId);
    @POST("rooms/{id}/invite")
    Call<ApiResponse> invite(@Path("id") long roomId, @Query("usersId") List<Long> usersId);

    @FormUrlEncoded
    @DELETE("rooms/kick")
    Call<ApiResponse> kickMember(@Field("memberId") long memberId);

    @DELETE("rooms/{id}/quit")
    Call<ApiResponse> quitRoom(@Path("id") long roomId);


    @GET("rooms/{id}/activities")
    Call<ApiResponse> getActivitiesOfRoom(@Path("id") long roomId);

    @Multipart
    @POST("rooms/{id}/chat")
    Call<ApiResponse> createChat(@Path("id") long roomId,
                                 @Part("content") RequestBody content,
                                 @Part MultipartBody.Part file);

    @FormUrlEncoded
    @PUT("rooms/chat/{id}")
    Call<ApiResponse> updateChat(@Path("id") long id,
                                 @Field("content") String content);

    @DELETE("rooms/chat/{id}")
    Call<ApiResponse> removeChat(@Path("id") long id);
}
