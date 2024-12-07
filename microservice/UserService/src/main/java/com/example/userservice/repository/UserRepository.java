package com.example.userservice.repository;

import com.example.userservice.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContaining(String name, Pageable pageable);
    Long countByNameContaining(String name);
    @Query("SELECT u FROM User u ORDER BY FUNCTION('RAND')")
    List<User> getRandomUser(Pageable pageable);
}