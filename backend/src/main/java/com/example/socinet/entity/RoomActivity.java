package com.example.socinet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomActivity extends AbstractEntity{
    String content;

    @Column(name = "file_url")
    String fileUrl;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    User sender;

    @ManyToOne
    @JoinColumn(name= "receiver_id")
    User receiver;

    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;

    String type;

    @Column(name = "is_active")
    boolean isActive;
}
