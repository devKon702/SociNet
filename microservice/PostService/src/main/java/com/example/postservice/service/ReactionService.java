package com.example.postservice.service;

import com.example.postservice.dto.ReactionDto;
import com.example.postservice.entity.Post;
import com.example.postservice.entity.React;
import com.example.postservice.entity.Reaction;
import com.example.postservice.repository.PostRepository;
import com.example.postservice.repository.ReactRepository;
import com.example.postservice.repository.ReactionRepository;
import com.example.postservice.request.ReactionRequest;
import com.example.postservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepo;
    private final PostRepository postRepo;
    private final ReactRepository reactRepo;

    public ReactionDto createReaction(ReactionRequest reactionRequest) throws Exception {
        Optional<Post> postOptional = postRepo.findById(reactionRequest.getPostId());
        if(postOptional.isEmpty()) throw new Exception("POST NOT FOUND");

        Optional<React> reactOptional = reactRepo.findById(reactionRequest.getType());
        if(reactOptional.isEmpty()) throw new Exception("TYPE NOT FOUND");

        Reaction reaction;
        Optional<Reaction> reactionOptional = reactionRepo.findByUser_IdAndPost_Id(Helper.getUserId(), reactionRequest.getPostId());
        // Nếu có thì cập nhật
        if(reactionOptional.isPresent()){
            reaction = reactionOptional.get();
            reaction.setReact(reactOptional.get());
        }
        // Không có thì tạo mới
        else{
            reaction = Reaction.builder()
                    .user(Helper.getAccountDetail().getUser())
                    .post(postOptional.get())
                    .react(reactOptional.get())
                    .build();
        }
        return new ReactionDto(reactionRepo.save(reaction));
    }

    public void deleteReaction(Long postId) throws Exception {
        Optional<Reaction> reactionOptional = reactionRepo.findByUser_IdAndPost_Id(Helper.getUserId(), postId);
        if(reactionOptional.isEmpty()) throw new Exception("REACTION NOT FOUND");
        reactionRepo.delete(reactionOptional.get());
    }
}
