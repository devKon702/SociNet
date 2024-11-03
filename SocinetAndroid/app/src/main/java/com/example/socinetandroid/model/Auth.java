package com.example.socinetandroid.model;

public class Auth {
    String accessToken;
    String refreshToken;
    Account account;

    public Auth(String accessToken, String refreshToken, Account account) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.account = account;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", account=" + account +
                '}';
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
