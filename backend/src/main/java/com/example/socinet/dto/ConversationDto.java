package com.example.socinet.dto;

import com.example.socinet.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDto {
    UserDto sender;
    UserDto receiver;
    String content;
    String fileUrl;
    @JsonProperty("isActive")
    boolean isActive;

    public ConversationDto(Conversation conversation){
        this.sender = new UserDto(conversation.getSender());
        this.receiver = new UserDto(conversation.getReceiver());
        this.content = conversation.getContent();
        this.fileUrl = conversation.getFileUrl();
        this.isActive = conversation.isActive();
    }
}
