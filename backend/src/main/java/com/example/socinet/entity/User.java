package com.example.socinet.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User extends AbstractEntity{
    @Column(nullable = false, length = 50)
    String name;
    @Column
    String phone;
    @Column
    String school;
    @Column
    String address;
    @Column(name = "avatar_url")
    String avatarUrl;
    @Column(name = "is_male")
    boolean isMale;
    @OneToOne(mappedBy = "user")
    Account account;
}
