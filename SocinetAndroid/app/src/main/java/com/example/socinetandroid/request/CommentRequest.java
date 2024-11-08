package com.example.socinetandroid.request;

public class CommentRequest {
    private Long postId;
    private String content;
    private Long parentCommentId;

    public CommentRequest(Long postId, String content, Long parentCommentId) {
        this.postId = postId;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }
}
