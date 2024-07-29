package com.example.socinet.repository;

import com.example.socinet.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByAccount_Username(String username);
    Optional<RefreshToken> findByAccount_UsernameAndToken(String username, String token);
}
