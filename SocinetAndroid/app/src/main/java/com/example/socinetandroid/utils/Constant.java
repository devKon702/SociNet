package com.example.socinetandroid.utils;

public class Constant {
    // API
    public static final String BASE_API_URL = "http://192.168.0.101:8080/api/v1/";
    // Token
    public static final String TOKEN_SHARED_PREFS_FILE = "private_prefs";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    // RESPONSE MESSAGE
    public static final String UNAUTHENTICATED_MESSAGE = "Unauthenticated";
    public static final String INVALID_TOKEN_MESSAGE = "INVALID TOKEN";
    public static final String EXPIRED_TOKEN_MESSAGE = "TOKEN EXPIRED";
}
