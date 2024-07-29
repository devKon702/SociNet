package com.example.socinet.dto;

import com.example.socinet.entity.Comment;
import com.example.socinet.entity.Post;
import com.example.socinet.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    UserDto user;
    String content;
    List<CommentDto> childComments;
    Date createdAt;
    Date updatedAt;

    public CommentDto(Comment comment){
        if(comment == null) return;
        this.id = comment.getId();
        this.user = new UserDto(comment.getUser());
        this.content = comment.getContent();
        this.childComments = new ArrayList<>();
        if(comment.getChildComments() != null) {
            comment.getChildComments().forEach(childComment -> this.childComments.add(new CommentDto(childComment)));
        }
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
