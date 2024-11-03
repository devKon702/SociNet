package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.model.ApiResponse;

public interface IRetrofitResponseHandler {
    void onSuccess(ApiResponse result);
    void onFail(ApiResponse result);
}
