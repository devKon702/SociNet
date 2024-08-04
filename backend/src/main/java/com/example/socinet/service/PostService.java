package com.example.socinet.service;

import com.example.socinet.dto.PostDto;
import com.example.socinet.entity.Post;
import com.example.socinet.repository.PostRepository;
import com.example.socinet.security.AccountDetail;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepo;
    private final FirebaseStorageService storageService;
    private final long MAX_IMAGE_SIZE = 3 * 1024 * 1024; // 3MB
    private final long MAX_VIDEO_SIZE = 15 * 1024 * 1024; //15MB

    public List<PostDto> getPosts(int page, int size){
        List<PostDto> postsDto = new ArrayList<>();
        postRepo.findAll().forEach(post -> postsDto.add(0, new PostDto(post)));
        return postsDto;
    }

    public PostDto getPost(Long id) throws Exception{
        Optional<Post> post = postRepo.findById(id);
        if(post.isEmpty()) throw new Exception("No post found");
        return new PostDto(post.get());
    }

    public List<PostDto> getPostsByUserId(Long userId, Pageable pageable) throws Exception{
        List<Post> posts = postRepo.findByUser_Id(userId, pageable);
        List<PostDto> postsDto = new ArrayList<>();
        posts.forEach(post -> postsDto.add(0, new PostDto(post)));
        return postsDto;
    }

    public PostDto createPost(String caption, MultipartFile image, MultipartFile video, Long sharedPostId) throws Exception{
        AccountDetail accountDetail = Helper.getAccountDetail();
        Post sharedPost = null;
        if(sharedPostId != null) {
            Optional<Post> sharedPostOptional = postRepo.findById(sharedPostId);
            if(sharedPostOptional.isEmpty()) throw new Exception("Shared post not found");
            else sharedPost = sharedPostOptional.get();
        }

        // Upload file lên firebase
        String imageUrl = "";
        String videoUrl = "";
        if(image != null && image.getSize() <= MAX_IMAGE_SIZE){
            imageUrl = storageService.upload("images", image);
        }

        if(video != null && video.getSize() <= MAX_VIDEO_SIZE){
            videoUrl = storageService.upload("videos", video);
        }

        Post post = Post.builder()
                .caption(caption)
                .user(accountDetail.getUser())
                .sharedPost(sharedPost)
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .isActive(true)
                .build();
        return new PostDto(postRepo.save(post));
    }

    public PostDto updatePost(Long postId, String caption, MultipartFile image, MultipartFile video) throws Exception{
        AccountDetail accountDetail = Helper.getAccountDetail();
        Optional<Post> postOptional = postRepo.findByIdAndUser_Id(postId, accountDetail.getUser().getId());
        if(postOptional.isEmpty()) throw new Exception("No post found");
        Post post = postOptional.get();
        post.setCaption(caption);
        if(image != null){
            String imageUrl = storageService.upload("images", image);
            post.setImageUrl(imageUrl);
        }
        if(video != null){
            String videoUrl = storageService.upload("images", video);
            post.setImageUrl(videoUrl);
        }
        return new PostDto(postRepo.save(post));
    }

    public void deleteUserPost(Long postId) throws Exception {
        Long userId = Helper.getUserId();
        Optional<Post> post = postRepo.findByIdAndUser_Id(postId, userId);
        if(post.isEmpty()) throw new Exception("No post found");
        postRepo.delete(post.get());
    }

}
