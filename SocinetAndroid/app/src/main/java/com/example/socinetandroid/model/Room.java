package com.example.socinetandroid.model;

import java.util.Date;
import java.util.List;

public class Room {
    private long id;
    private String name;
    private String code;
    private String avatarUrl;
    private List<RoomMember> members;
    private boolean isActive;
    private boolean isAdmin = false;
    private Date createdAt;
    private Date updatedAt;

    public Room(long id, String name, String code, String avatarUrl, List<RoomMember> members, boolean isActive, boolean isAdmin, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.avatarUrl = avatarUrl;
        this.members = members;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<RoomMember> getMembers() {
        return members;
    }

    public void setMembers(List<RoomMember> members) {
        this.members = members;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
