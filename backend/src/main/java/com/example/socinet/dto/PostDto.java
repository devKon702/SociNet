package com.example.socinet.dto;

import com.example.socinet.entity.Comment;
import com.example.socinet.entity.Post;
import com.example.socinet.entity.Reaction;
import com.example.socinet.util.Helper;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDto {
    Long id;
    UserDto user;
    String caption;
    String imageUrl;
    String videoUrl;
    PostDto sharedPost;
    int numberOfComments;
    Map<String, Integer> numberOfReactions;
    @JsonProperty("isActive")
    boolean isActive;
    Date createdAt;
    Date updatedAt;
    String selfReaction;

    public PostDto(Post post){
        if(post == null) return;
        this.id = post.getId();
        this.user = new UserDto(post.getUser());
        this.caption = post.getCaption();
        this.imageUrl = post.getImageUrl();
        this.videoUrl = post.getVideoUrl();
        this.sharedPost = post.getSharedPost() == null ? null : new PostDto(post.getSharedPost());
        this.numberOfComments = post.getComments() != null ? post.getComments().size() : 0;
        this.numberOfReactions = new HashMap<>();
        if(post.getReactions() != null) {
            post.getReactions().forEach(reaction -> {
                String type = reaction.getReact().getType();
                this.numberOfReactions.put(type, this.numberOfReactions.getOrDefault(type, 0) + 1);
                Long userId = reaction.getUser().getId();
                if(userId == Helper.getUserId()) this.selfReaction = type;
            });
        }
        this.isActive = post.isActive();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
