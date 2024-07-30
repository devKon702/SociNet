package com.example.socinet.dto;

import com.example.socinet.entity.Friend;
import com.example.socinet.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendDto {
    Long id;
    UserDto user;
    Date createdAt;
    Date updatedAt;
    @JsonProperty("isAccepted")
    boolean isAccepted;

    public FriendDto(Friend friend){
        this.id = friend.getId();
        this.createdAt = friend.getCreatedAt();
        this.updatedAt = friend.getUpdatedAt();
        this.isAccepted = friend.isAccepted();
        Long currentUserId = Helper.getUserId();
        if(friend.getSender().getId() != currentUserId){
            this.user = new UserDto(friend.getSender());
        } else this.user = new UserDto(friend.getReceiver());
    }
}
