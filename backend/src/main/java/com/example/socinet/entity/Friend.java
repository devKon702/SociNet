package com.example.socinet.entity;

import jakarta.persistence.*;
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
