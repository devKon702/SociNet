package com.example.chatservice.repository;

import com.example.chatservice.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContaining(String name, Pageable pageable);
    Long countByNameContaining(String name);
}