package com.example.socinetandroid.service.config;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.socinetandroid.utils.TokenManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final Context context;
    private final TokenManager tokenManager;
    public PersistentCookieJar(Context context) {
//        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
//        sharedPreferences = EncryptedSharedPreferences.create(
//                PREF_NAME,
//                masterKeyAlias,
//                context,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        );
//        Set<String> cookieStrings = sharedPreferences.getStringSet(KEY_COOKIES, new HashSet<>());
        this.context = context;
        this.tokenManager = new TokenManager(context);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)  {
        Set<String> cookieStrings = new HashSet<>();
        if(cookies == null || cookies.isEmpty()) return;
        for (Cookie cookie : cookies) {
            cookieStrings.add(cookie.toString());
            if(cookie.name().equals(REFRESH_TOKEN_COOKIE_NAME)){
                String token = cookie.toString();
                tokenManager.saveCookieRefreshToken(cookie.toString());
                break;
            }
        }
//        sharedPreferences.edit().putStringSet(KEY_COOKIES, cookieStrings).apply();
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
//        Set<String> cookieStrings = sharedPreferences.getStringSet(KEY_COOKIES, new HashSet<>());
//        List<Cookie> cookies = new ArrayList<>();
//        for (String cookieString : cookieStrings) {
//            cookies.add(Cookie.parse(url, cookieString));
//        }
//        return cookies;
        List<Cookie> cookies = new ArrayList<>();
        String refreshToken = tokenManager.getCookieRefreshToken();
        if(refreshToken != null) {
            Cookie cookie = Cookie.parse(url, refreshToken);
            if(cookie != null) cookies.add(cookie);
            return cookies;
        };
        return cookies;
    }
}
