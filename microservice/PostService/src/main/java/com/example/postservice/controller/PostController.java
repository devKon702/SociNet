package com.example.postservice.controller;

import com.example.postservice.dto.PostDto;
import com.example.postservice.security.AccountDetail;
import com.example.postservice.service.PostService;
import com.example.postservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        List<PostDto> posts = postService.getPosts(page,size);
        return Helper.returnSuccessResponse("Get post list success", posts);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) throws Exception{
        PostDto postDto = postService.getPost(id);
        return Helper.returnSuccessResponse("Get post success", postDto);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> getPostByUser(@PathVariable(required = false) Long id, Pageable pageable) throws Exception{
        // Nếu không cung cấp id, lấy của user hiện tại
        if(id == null){
            AccountDetail accountDetail = Helper.getAccountDetail();
            id = accountDetail.getUser().getId();
        }
        List<PostDto> posts = postService.getPostsByUserId(id, pageable);
        return Helper.returnSuccessResponse("Get posts by user success", posts);
    }

    @PostMapping("")
    public ResponseEntity<?> createPost(@RequestParam String caption,
                                        @RequestParam(required = false) MultipartFile file,
                                        @RequestParam(required = false) Long sharedPostId) throws Exception {
        PostDto createdPost = postService.createPost(caption, file, sharedPostId);
        return Helper.returnSuccessResponse("Create post success", createdPost);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestParam(required = false) String caption,
                                        @RequestParam(required = false) MultipartFile file) throws Exception{
        PostDto updatedPost = postService.updatePost(id, caption, file);
        return Helper.returnSuccessResponse("Update post success", updatedPost);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUserPost(@PathVariable Long id) throws Exception {
        postService.deleteUserPost(id);
        return Helper.returnSuccessResponse("Delete post success", null);
    }
}
