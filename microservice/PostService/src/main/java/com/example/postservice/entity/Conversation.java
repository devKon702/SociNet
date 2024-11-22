package com.example.postservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "senderId")
    User sender;

    @ManyToOne
    @JoinColumn(name = "receiverId")
    User receiver;

    @Column
    String content;

    @Column
    String fileUrl;

    @Column
    boolean isActive;
}
