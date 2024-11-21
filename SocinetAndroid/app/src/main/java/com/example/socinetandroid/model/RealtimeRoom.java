package com.example.socinetandroid.model;

import com.example.socinetandroid.enums.RealtimeStatus;

public class RealtimeRoom {
    private Room room;
    private String newMessage;
    private boolean hasUnread;

    public RealtimeRoom(Room room, String newMessage, boolean hasUnread) {
        this.room = room;
        this.newMessage = newMessage;
        this.hasUnread = hasUnread;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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
