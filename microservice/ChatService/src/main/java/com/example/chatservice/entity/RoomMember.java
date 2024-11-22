package com.example.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "room_id"})})
public class RoomMember extends AbstractEntity{
    @ManyToOne()
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;

    @Column(name = "is_admin")
    boolean isAdmin;
}
