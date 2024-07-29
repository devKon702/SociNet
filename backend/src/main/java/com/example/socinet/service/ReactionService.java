package com.example.socinet.service;

import com.example.socinet.dto.ReactionDto;
import com.example.socinet.entity.Post;
import com.example.socinet.entity.React;
import com.example.socinet.entity.Reaction;
import com.example.socinet.repository.PostRepository;
import com.example.socinet.repository.ReactRepository;
import com.example.socinet.repository.ReactionRepository;
import com.example.socinet.request.ReactionRequest;
import com.example.socinet.util.Helper;
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
        if(postOptional.isEmpty()) throw new Exception("Post not found");

        Optional<React> reactOptional = reactRepo.findById(reactionRequest.getType());
        if(reactOptional.isEmpty()) throw new Exception("Reaction's type not found");

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
        if(reactionOptional.isEmpty()) throw new Exception("Reaction not found");
        reactionRepo.delete(reactionOptional.get());
    }
}
