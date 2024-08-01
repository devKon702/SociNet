package com.example.socinet.dto;

import com.example.socinet.entity.Conversation;
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
    Long receiverId;
    String content;
    String fileUrl;
    Date createdAt;
    Date updatedAt;
    @JsonProperty("isActive")
    boolean isActive;


    public ConversationDto(Conversation conversation){
        this.id = conversation.getId();
        this.senderId = conversation.getSender().getId();
        this.receiverId = conversation.getReceiver().getId();
        this.content = conversation.getContent();
        this.fileUrl = conversation.getFileUrl();
        this.createdAt = conversation.getCreatedAt();
        this.updatedAt = conversation.getUpdatedAt();
        this.isActive = conversation.isActive();
    }
}
