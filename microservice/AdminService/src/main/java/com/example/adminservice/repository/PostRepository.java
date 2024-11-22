package com.example.adminservice.repository;

import com.example.adminservice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser_Id(Long userId, Pageable pageable);
    Optional<Post> findByIdAndUser_Id(Long id, Long userId);
    long countByUser_Id(Long userId);
}
