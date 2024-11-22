package com.example.chatservice.dto;

import com.example.chatservice.entity.RoomActivity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomActivityDto {
    Long id;
    String content;
    String fileUrl;
    UserDto sender;
    UserDto receiver;
    Long roomId;
    String type;
    @JsonProperty("isActive")
    boolean isActive;
    Date createdAt;
    Date updatedAt;

    public RoomActivityDto(RoomActivity activity){
        this.id = activity.getId();
        this.content = activity.getContent();
        this.fileUrl = activity.getFileUrl();
        this.sender = new UserDto(activity.getSender());
        if(activity.getReceiver() != null) {
            this.receiver = new UserDto(activity.getReceiver());
        }
        this.roomId = activity.getRoom().getId();
        this.type = activity.getType();
        this.isActive = activity.isActive();
        this.createdAt = activity.getCreatedAt();
        this.updatedAt = activity.getUpdatedAt();
    }
}
