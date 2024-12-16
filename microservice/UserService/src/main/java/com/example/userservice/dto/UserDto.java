package com.example.userservice.dto;

import com.example.userservice.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UserDto {
    Long id;
    String name;
    String phone;
    String school;
    String address;
    String avatarUrl;
    @JsonProperty("isMale")
    boolean isMale;
    public UserDto(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.school = user.getSchool();
        this.address = user.getAddress();
        this.avatarUrl = user.getAvatarUrl();
        this.isMale = user.isMale();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserDto tmp = (UserDto) obj;
        return id.equals(tmp.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
