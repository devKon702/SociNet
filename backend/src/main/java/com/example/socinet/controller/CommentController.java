package com.example.socinet.controller;

import com.example.socinet.dto.CommentDto;
import com.example.socinet.entity.Comment;
import com.example.socinet.request.CommentRequest;
import com.example.socinet.service.CommentService;
import com.example.socinet.util.Helper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("post/{id}")
    public ResponseEntity<?> getCommentByPostId(@PathVariable Long id){
        List<CommentDto> comments = commentService.getCommentByPostId(id);
        return Helper.returnSuccessResponse("Get post's comments success", comments);
    }

    @PostMapping("")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest commentRequest) throws Exception{
        CommentDto comment = commentService.createComment(commentRequest);
        return Helper.returnSuccessResponse("Create comment success", comment);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @NotBlank(message = "Comment's content must not be blank")
                                           @RequestParam String content) throws Exception{
        CommentDto commentDto = commentService.updateComment(id, content);
        return Helper.returnSuccessResponse("Update comment success", commentDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) throws Exception{
        commentService.deleteComment(id);
        return Helper.returnSuccessResponse("Delete comment success", null);
    }

}
