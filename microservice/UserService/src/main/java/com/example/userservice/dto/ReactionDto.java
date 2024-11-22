package com.example.userservice.dto;

import com.example.userservice.entity.Reaction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionDto {
    UserDto user;
    String reactType;

    // Update
    PostDto post;

    public ReactionDto(Reaction reaction){
        this.user = new UserDto(reaction.getUser());
        this.reactType = reaction.getReact().getType();
        // Update
        this.post = new PostDto(reaction.getPost());
    }
}
