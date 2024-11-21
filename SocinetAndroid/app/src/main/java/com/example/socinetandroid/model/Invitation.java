package com.example.socinetandroid.model;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Objects;

public class Invitation {
    public static String STATUS_FRIEND = "FRIEND";
    public static String STATUS_INVITED = "INVITED";
    public static String STATUS_STRANGE = "NO";
    private long id;
    private User sender;
    private User receiver;
    private Date createdAt;
    private Date updatedAt;
    boolean isAccepted;

    public Invitation(long id, User sender, User receiver, Date createdAt, Date updatedAt, boolean isAccepted) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAccepted = isAccepted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
