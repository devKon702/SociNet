package com.example.socinet.dto;

import com.example.socinet.entity.Room;
import com.example.socinet.entity.RoomMember;
import com.example.socinet.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {
    Long id;
    String name;
    String code;
    String avatarUrl;
    List<RoomMemberDto> members;
    @JsonProperty("isActive")
    boolean isActive;
    @JsonProperty("isAdmin")
    boolean isAdmin = false;
    Date createdAt;
    Date updatedAt;

    public RoomDto(Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.code = room.getCode();
        this.avatarUrl = room.getAvatarUrl();
        this.members = new ArrayList<>();
        if(room.getMembers() != null) {
            room.getMembers().forEach((member) -> {
                this.members.add(new RoomMemberDto(member));
                if(member.isAdmin() && Helper.getUserId() == member.getUser().getId()) this.isAdmin = true;
            });
        }
        this.isActive = room.isActive();
        this.createdAt = room.getCreatedAt();
        this.updatedAt = room.getUpdatedAt();
    }
}
