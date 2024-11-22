package com.example.postservice.service;

import com.example.postservice.dto.CommentDto;
import com.example.postservice.entity.Comment;
import com.example.postservice.entity.Post;
import com.example.postservice.repository.CommentRepository;
import com.example.postservice.repository.PostRepository;
import com.example.postservice.request.CommentRequest;
import com.example.postservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepo;
    private final PostRepository postRepo;

    public List<CommentDto> getCommentByPostId(Long postId){
        List<Comment> comments = commentRepo.findAllByPost_Id(postId);
        List<CommentDto> commentsDto = new ArrayList<>();
        comments.forEach(comment -> {
            // Không lấy các comment có comment cha
            if(comment.getParentComment() != null) return;
            // reverse
            commentsDto.add(0, new CommentDto(comment));
        });
        return commentsDto;
    }

    public CommentDto createComment(CommentRequest commentRequest) throws Exception {
        // Kiểm tra post có tồn tại
        Optional<Post> postOptional = postRepo.findById(commentRequest.getPostId());
        if(postOptional.isEmpty()) throw new Exception("POST NOT FOUND");

        // Kiểm tra parent comment (nếu có) có tồn tại
        Optional<Comment> parentOptional = Optional.empty();
        if(commentRequest.getParentCommentId() != null){
            parentOptional = commentRepo.findById(commentRequest.getParentCommentId());
            if(parentOptional.isEmpty()) throw new Exception("PARENT COMMENT NOT FOUND");
        }

        Comment comment = Comment.builder()
                .user(Helper.getAccountDetail().getUser())
                .post(postOptional.get())
                .content(commentRequest.getContent())
                .parentComment(parentOptional.isPresent() ? parentOptional.get() : null)
                .build();
        return new CommentDto(commentRepo.save(comment));
    }

    public CommentDto updateComment(Long commentId, String content) throws Exception {
        Optional<Comment> commentOptional = commentRepo.findById(commentId);
        if(commentOptional.isEmpty()) throw new Exception("COMMENT NOT FOUND");
        Comment comment = commentOptional.get();
        comment.setContent(content);
        return new CommentDto(commentRepo.save(comment));
    }

    public void deleteComment(Long commentId) throws Exception{
        Long userId = Helper.getUserId();
        Optional<Comment> commentOptional = commentRepo.findByIdAndUser_Id(commentId, userId);
        if(commentOptional.isEmpty()) throw new Exception("COMMENT NOT FOUND");
        commentRepo.delete(commentOptional.get());
    }
}
