package com.example.adminservice.service;

import com.example.adminservice.dto.PostDto;
import com.example.adminservice.entity.Post;
import com.example.adminservice.repository.PostRepository;
import com.example.adminservice.security.AccountDetail;
import com.example.adminservice.util.Helper;
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
        postRepo.findAll().forEach(post -> {
            if(post.isActive()){
                postsDto.add(0, new PostDto(post));
            }
        });
        return postsDto;
    }

    public PostDto getPost(Long id) throws Exception{
        Optional<Post> post = postRepo.findById(id);
        if(post.isEmpty()) throw new Exception("POST NOT FOUND");
        return new PostDto(post.get());
    }

    public List<PostDto> getPostsByUserId(Long userId, Pageable pageable) throws Exception{
        List<Post> posts = postRepo.findByUser_Id(userId, pageable);
        List<PostDto> postsDto = new ArrayList<>();
        posts.forEach(post -> postsDto.add(0, new PostDto(post)));
        return postsDto;
    }

    public PostDto createPost(String caption, MultipartFile file, Long sharedPostId) throws Exception{
        AccountDetail accountDetail = Helper.getAccountDetail();
        Post sharedPost = null;
        if(sharedPostId != null) {
            Optional<Post> sharedPostOptional = postRepo.findById(sharedPostId);
            if(sharedPostOptional.isEmpty()) throw new Exception("SHARED POST NOT FOUND");
            else sharedPost = sharedPostOptional.get();
        }

        // Upload file lên firebase
        String fileUrl = "";
        if(file != null && file.getContentType().startsWith("image")){
            if(file.getSize() > MAX_IMAGE_SIZE) throw new Exception("OVERSIZE IMAGE");
            fileUrl = storageService.upload("images", file);
        }

        if(file != null && file.getContentType().startsWith("video")){
            if(file.getSize() > MAX_VIDEO_SIZE) throw new Exception("OVERSIZE VIDEO");
            fileUrl = storageService.upload("videos", file);
        }
        // Type of file is unsupported
        if(file != null && fileUrl=="") throw new Exception("UNSUPPORTED FILE");

        Post post = Post.builder()
                .caption(caption)
                .user(accountDetail.getUser())
                .sharedPost(sharedPost)
                .imageUrl(fileUrl)
                .isActive(true)
                .build();
        return new PostDto(postRepo.save(post));
    }

    public PostDto updatePost(Long postId, String caption, MultipartFile file) throws Exception{
        AccountDetail accountDetail = Helper.getAccountDetail();
        Optional<Post> postOptional = postRepo.findByIdAndUser_Id(postId, accountDetail.getUser().getId());
        if(postOptional.isEmpty()) throw new Exception("POST NOT FOUND");
        Post post = postOptional.get();
        post.setCaption(caption);
        // Upload file lên firebase
        String fileUrl = "";
        if(file != null && file.getContentType().startsWith("image")){
            if(file.getSize() > MAX_IMAGE_SIZE) throw new Exception("OVERSIZE IMAGE");
            fileUrl = storageService.upload("images", file);
            post.setImageUrl(fileUrl);
        }

        if(file != null && file.getContentType().startsWith("video")){
            if(file.getSize() > MAX_VIDEO_SIZE) throw new Exception("OVERSIZE VIDEO");
            fileUrl = storageService.upload("videos", file);
            post.setImageUrl(fileUrl);
        }
        // Type of file is unsupported
        if(file != null && fileUrl=="") throw new Exception("UNSUPPORTED FILE");

        return new PostDto(postRepo.save(post));
    }

    public void deleteUserPost(Long postId) throws Exception {
        Long userId = Helper.getUserId();
        Optional<Post> post = postRepo.findByIdAndUser_Id(postId, userId);
        if(post.isEmpty()) throw new Exception("POST NOT FOUND");
        postRepo.delete(post.get());
    }

}
