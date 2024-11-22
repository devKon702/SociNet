package com.example.chatservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Friend extends AbstractEntity{
    @ManyToOne()
    @JoinColumn(name = "senderId")
    User sender;

    @ManyToOne()
    @JoinColumn(name = "receiverId")
    User receiver;

    @Column
    boolean isAccepted;
}
