package com.example.socinetandroid.model;

import com.example.socinetandroid.enums.RealtimeStatus;

public class RealtimeUser {
    private User user;
    private RealtimeStatus status;
    private String newMessage;
    private boolean hasUnread;

    public RealtimeUser(User user, RealtimeStatus status, String newMessage, boolean hasUnread) {
        this.user = user;
        this.status = status;
        this.newMessage = newMessage;
        this.hasUnread = hasUnread;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RealtimeStatus getStatus() {
        return status;
    }

    public void setStatus(RealtimeStatus status) {
        this.status = status;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public boolean isHasUnread() {
        return hasUnread;
    }

    public void setHasUnread(boolean hasUnread) {
        this.hasUnread = hasUnread;
    }
}
