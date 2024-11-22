package com.example.postservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class Room extends AbstractEntity{
    String name;

    @Column(unique = true)
    String code;

    @Column(name = "avatar_url")
    String avatarUrl;

    @OneToMany(mappedBy = "room")
    List<RoomMember> members;

    boolean isActive;
}
