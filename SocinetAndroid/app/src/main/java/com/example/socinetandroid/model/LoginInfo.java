package com.example.socinetandroid.model;

import java.util.Date;

public class LoginInfo {
    private Long id;
    private String userAgent;
    private String ip;
    private Date createdAt;
    private Date updatedAt;

    public LoginInfo(Long id, String userAgent, String ip, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userAgent = userAgent;
        this.ip = ip;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIp() {
        return ip;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
