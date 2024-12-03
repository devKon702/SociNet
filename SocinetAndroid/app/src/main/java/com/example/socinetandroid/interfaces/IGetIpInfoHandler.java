package com.example.socinetandroid.interfaces;

import com.example.socinetandroid.model.IPResponse;

public interface IGetIpInfoHandler {
    void onSuccess(IPResponse response);
    void onFail();
}
