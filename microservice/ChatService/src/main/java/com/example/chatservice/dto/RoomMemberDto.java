package com.example.chatservice.dto;

import com.example.chatservice.entity.RoomMember;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomMemberDto {
    Long id;
    UserDto user;
    @JsonProperty("isAdmin")
    boolean isAdmin;

    public RoomMemberDto(RoomMember member){
        this.id = member.getId();
        this.user = new UserDto(member.getUser());
        this.isAdmin = member.isAdmin();
    }
}
