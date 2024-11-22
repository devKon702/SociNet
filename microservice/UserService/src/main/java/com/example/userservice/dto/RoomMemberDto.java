package com.example.userservice.dto;

import com.example.userservice.entity.RoomMember;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomMemberDto {
    UserDto user;
    @JsonProperty("isAdmin")
    boolean isAdmin;

    public RoomMemberDto(RoomMember member){
        this.user = new UserDto(member.getUser());
        this.isAdmin = member.isAdmin();
    }
}
