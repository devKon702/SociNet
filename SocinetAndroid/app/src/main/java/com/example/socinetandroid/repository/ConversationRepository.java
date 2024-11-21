package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.ApiResponse;

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

public interface ConversationRepository {
    @GET("conversations")
    Call<ApiResponse> getChatWithUser(@Query("userId") long userId);

    @Multipart
    @POST("conversations")
    Call<ApiResponse> createChatWithUser(@Part("receiverId") RequestBody userId,
                                         @Part("content") RequestBody content,
                                         @Part MultipartBody.Part file);

    @FormUrlEncoded
    @PUT("conversations/{id}")
    Call<ApiResponse> updateChat(@Path("id") long id, @Field("content") String content);

    @DELETE("conversations/{id}")
    Call<ApiResponse> removeChat(@Path("id") long id);
}
