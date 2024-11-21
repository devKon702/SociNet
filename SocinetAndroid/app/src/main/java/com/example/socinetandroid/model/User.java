package com.example.socinetandroid.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class User {
    private long id;
    private String name;
    private String phone;
    private String school;
    private String address;
    private String avatarUrl;
    private boolean isMale;

    public User(long id, String name, String phone, String school, String address, String avatarUrl, boolean isMale) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.school = school;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.isMale = isMale;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", school='" + school + '\'' +
                ", address='" + address + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", isMale=" + isMale +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
