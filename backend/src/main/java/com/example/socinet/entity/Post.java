package com.example.socinet.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @Column
    String caption;
    @Column(name = "image_url")
    String imageUrl;
    @Column(name = "video_url")
    String videoUrl;

    @ManyToOne
    @JoinColumn(name = "shared_post_id")
    Post sharedPost;

    @OneToMany(mappedBy = "sharedPost")
    List<Post> postShare;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    List<Reaction> reactions;

    @Column(name = "is_active")
    boolean isActive;
    @PreRemove
    private void preRemove() {
        for (Post post : postShare) {
            post.setSharedPost(null);
        }
    }

}
