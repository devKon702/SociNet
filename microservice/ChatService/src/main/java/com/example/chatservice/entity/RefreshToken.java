package com.example.chatservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "username")
    Account account;

    @Column
    String token;

    @Column(name = "user_agent")
    String userAgent;

    @Column
    String ip;
}
