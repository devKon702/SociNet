package com.example.socinetandroid.model;

import java.util.Date;

public class RoomsActivity {
    public static final String CHAT = "CHAT";
    public static final String NOTIFY = "NOTIFY";
    public static final String INVITE = "INVITE";
    public static final String JOIN = "JOIN";
    public static final String QUIT = "QUIT";
    public static final String KICK = "KICK";
    private long id;
    private String content;
    private String fileUrl;
    private User sender;
    private User receiver;
    private long roomId;
    private String type;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    public RoomsActivity(long id, String content, String fileUrl, User sender, User receiver, long roomId, String type, boolean isActive, Date createdAt, Date updatedAt) {
        this.id = id;
        this.content = content;
        this.fileUrl = fileUrl;
        this.sender = sender;
        this.receiver = receiver;
        this.roomId = roomId;
        this.type = type;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
