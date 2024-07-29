package com.example.socinet.repository;

import com.example.socinet.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_Id(Long postId);
    Optional<Comment> findByIdAndUser_Id(Long id, Long userId);
}
