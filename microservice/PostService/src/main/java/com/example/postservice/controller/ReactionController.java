package com.example.postservice.controller;

import com.example.postservice.dto.ReactionDto;
import com.example.postservice.request.ReactionRequest;
import com.example.postservice.service.ReactionService;
import com.example.postservice.util.Helper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reactions")
@AllArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("")
    public ResponseEntity<?> createReaction(@Valid @RequestBody ReactionRequest reactionRequest) throws Exception {
        ReactionDto reaction = reactionService.createReaction(reactionRequest);
        return Helper.returnSuccessResponse("Create reaction success", reaction);
    }

    @DeleteMapping("post/{postId}")
    public ResponseEntity<?> deleteReactionByPostId(@PathVariable Long postId) throws Exception{
        reactionService.deleteReaction(postId);
        return Helper.returnSuccessResponse("Delete reaction success", null);
    }
}
