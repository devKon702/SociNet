package com.example.socinetandroid.model;

import android.support.annotation.NonNull;

import androidx.annotation.Nullable;

import com.example.socinetandroid.enums.RealtimeStatus;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

public class RealtimeChat {
    public final static String SINGLE_CHAT = "SingleChat";
    public final static String ROOM_CHAT = "RoomChat";
    private long id; // Can be userId if pass User, roomId if pass Room
    private String name;
    private String avatarUrl;
    private String newMessage;
    private String chatType;
    private boolean hasNewMessage;
    private RealtimeStatus status;

    public RealtimeChat(long id, String name, String avatarUrl, String newMessage, String chatType, boolean hasNewMessage, RealtimeStatus status) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        if(newMessage == null) this.newMessage = "";
        else this.newMessage = newMessage;
        this.chatType = chatType;
        this.hasNewMessage = hasNewMessage;
        this.status = status;
    }

    public RealtimeChat(@NonNull User user, String newMessage, boolean hasNewMessage, RealtimeStatus status){
        this.id = user.getId();
        this.name = user.getName();
        this.avatarUrl = user.getAvatarUrl();
        this.chatType = SINGLE_CHAT;
        this.newMessage = newMessage;
        this.hasNewMessage = hasNewMessage;
        this.status = status;
    }

    public RealtimeChat(@NonNull Room room, String newMessage, boolean hasNewMessage){
        this.id = room.getId();
        this.name = room.getName();
        this.avatarUrl = room.getAvatarUrl();
        this.chatType = ROOM_CHAT;
        this.newMessage = newMessage;
        this.hasNewMessage = hasNewMessage;
        this.status = RealtimeStatus.STRANGE;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }
    public RealtimeStatus getStatus() {
        return status;
    }

    public void setStatus(RealtimeStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RealtimeChat item = (RealtimeChat) obj;

        // Compare each field
        return id == item.id &&
                hasNewMessage == item.hasNewMessage &&
                Objects.equals(name, item.name) &&
                Objects.equals(avatarUrl, item.avatarUrl) &&
                Objects.equals(newMessage, item.newMessage) &&
                Objects.equals(chatType, item.chatType) &&
                status.equals(item.status);
    }
}
