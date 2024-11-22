package com.example.postservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    Post post;

    @Column
    String content;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> childComments;
}
