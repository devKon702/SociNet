package com.example.userservice.dto;

import com.example.userservice.entity.Friend;
import com.example.userservice.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendInvitationDto {
    Long id;
    UserDto user;
    UserDto sender;
    UserDto receiver;
    Date createdAt;
    Date updatedAt;
    @JsonProperty("isAccepted")
    boolean isAccepted;

    public FriendInvitationDto(Friend friend){
        this.id = friend.getId();
        this.createdAt = friend.getCreatedAt();
        this.updatedAt = friend.getUpdatedAt();
        this.isAccepted = friend.isAccepted();
        Long currentUserId = Helper.getUserId();
        if(friend.getSender().getId() != currentUserId){
            this.user = new UserDto(friend.getSender());
        } else this.user = new UserDto(friend.getReceiver());
        this.sender = new UserDto(friend.getSender());
        this.receiver = new UserDto(friend.getReceiver());
    }
}
