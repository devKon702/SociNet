package com.example.userservice.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserItemProfile {
    public UserItemProfile(UserDto user){
        this.user = user;
        sameFriend = 0;
        sameGroup = 0;
        interacted = 0;
        chatted = 0;
    }
    UserDto user;
    int sameFriend;
    int sameGroup;
    int interacted;
    int chatted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserItemProfile that = (UserItemProfile) o;
        return Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
