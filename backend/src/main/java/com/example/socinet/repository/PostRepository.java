package com.example.socinet.repository;

import com.example.socinet.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser_Id(Long userId);
    Optional<Post> findByIdAndUser_Id(Long id, Long userId);
}
