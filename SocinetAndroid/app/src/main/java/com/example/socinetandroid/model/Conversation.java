package com.example.socinetandroid.model;

import java.util.Date;

public class Conversation {
    private long id;
    private long senderId;
    private User sender;
    private long receiverId;
    private User receiver;
    private String content;
    private String fileUrl;
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;

    public Conversation(long id, User sender, User receiver, String content, String fileUrl, Date createdAt, Date updatedAt, boolean isActive) {
        this.id = id;
        this.senderId = sender.getId();
        this.sender = sender;
        this.receiverId = receiver.getId();
        this.receiver = receiver;
        this.content = content;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
