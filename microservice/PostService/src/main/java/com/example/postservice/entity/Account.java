package com.example.postservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @Column(unique = true)
    String username;

    @Column
    String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_username"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @Column(nullable = false)
    String email;

    @Column(name = "is_email_auth")
    boolean isEmailAuth;

    @Column(name = "is_active")
    boolean isActive;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
