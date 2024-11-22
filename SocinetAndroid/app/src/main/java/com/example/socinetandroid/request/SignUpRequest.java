package com.example.socinetandroid.request;

public class SignUpRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String otp;

    public SignUpRequest(String username, String password, String name, String email, String otp) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.otp = otp;
    }
}
