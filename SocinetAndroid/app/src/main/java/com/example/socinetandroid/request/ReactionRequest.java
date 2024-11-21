package com.example.socinetandroid.request;

public class ReactionRequest {
    private Long postId;
    private String type;

    public ReactionRequest(Long postId, String type) {
        this.postId = postId;
        this.type = type;
    }
}
