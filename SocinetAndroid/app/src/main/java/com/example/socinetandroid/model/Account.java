package com.example.socinetandroid.model;

import java.util.Set;

public class Account {
    String username;
    String email;
    boolean isEmailAuth;
    boolean isActive;
    Set<String> roles;
    User user;

    public Account(String username, String email, boolean isEmailAuth, boolean isActive, Set<String> roles, User user) {
        this.username = username;
        this.email = email;
        this.isEmailAuth = isEmailAuth;
        this.isActive = isActive;
        this.roles = roles;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isEmailAuth=" + isEmailAuth +
                ", isActive=" + isActive +
                ", roles=" + roles +
                ", user=" + user +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailAuth() {
        return isEmailAuth;
    }

    public void setEmailAuth(boolean emailAuth) {
        isEmailAuth = emailAuth;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
