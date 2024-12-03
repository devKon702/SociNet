package com.example.socinetandroid.repository;

import com.example.socinetandroid.model.IPResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IpRepository {
    @GET("json/{ip}")
    Call<IPResponse> getIpInfo(@Path("ip")String ip);
    @GET("json")
    Call<IPResponse> getCurrentIpInfo(@Query("fields") String fields);
}
