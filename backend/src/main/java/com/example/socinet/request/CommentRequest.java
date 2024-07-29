package com.example.socinet.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotNull
    Long postId;
    @NotBlank(message = "Comment must not be blank")
    String content;
    Long parentCommentId;
}
