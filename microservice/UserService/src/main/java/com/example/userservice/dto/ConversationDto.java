package com.example.userservice.dto;

import com.example.userservice.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDto {
    Long id;
    Long senderId;
    UserDto sender;
    Long receiverId;
    UserDto receiver;
    String content;
    String fileUrl;
    Date createdAt;
    Date updatedAt;
    @JsonProperty("isActive")
    boolean isActive;


    public ConversationDto(Conversation conversation){
        this.id = conversation.getId();
        this.senderId = conversation.getSender().getId();
        this.sender = new UserDto(conversation.getSender());
        this.receiverId = conversation.getReceiver().getId();
        this.receiver = new UserDto(conversation.getReceiver());
        this.content = conversation.getContent();
        this.fileUrl = conversation.getFileUrl();
        this.createdAt = conversation.getCreatedAt();
        this.updatedAt = conversation.getUpdatedAt();
        this.isActive = conversation.isActive();
    }
}
