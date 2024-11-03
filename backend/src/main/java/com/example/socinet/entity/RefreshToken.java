package com.example.socinet.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

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
