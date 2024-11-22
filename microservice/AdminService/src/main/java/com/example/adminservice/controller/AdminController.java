package com.example.adminservice.controller;

import com.example.adminservice.dto.AccountDto;
import com.example.adminservice.dto.PostDto;
import com.example.adminservice.service.AdminService;
import com.example.adminservice.service.PostService;
import com.example.adminservice.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final PostService postService;

    @GetMapping("accounts")
    public ResponseEntity<?> getAccountsByName(@RequestParam(defaultValue = "") String name, Pageable pageable){
        List<AccountDto> accountsDto = adminService.getAccountsByName(name, pageable);
        return Helper.returnSuccessResponse("Get account list success", accountsDto);
    }

    @GetMapping("count/accounts")
    public ResponseEntity<?> getNumberOfAccountsByName(@RequestParam(defaultValue = "") String name){
        long count = adminService.getNumberOfAccountByName(name);
        return Helper.returnSuccessResponse("Count account success", count);
    }

    @GetMapping("accounts/{username}")
    public ResponseEntity<?> getAccount(@PathVariable String username) throws Exception{
        AccountDto accountDto = adminService.getAccount(username);
        return Helper.returnSuccessResponse("Get account success", accountDto);
    }

    @GetMapping("posts")
    public ResponseEntity<?> getPostsByUserId(@RequestParam Long userId, Pageable pageable) throws Exception {

        List<PostDto> posts = postService.getPostsByUserId(userId, pageable);
        return Helper.returnSuccessResponse("Get post list success", posts);
    }

    @GetMapping("count/posts")
    public ResponseEntity<?> getNumberOfPostByUserId(@RequestParam Long userId){
        Long count = adminService.getNumberOfPostByUserId(userId);
        return Helper.returnSuccessResponse("Count post success", count);
    }

    @GetMapping("posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) throws Exception{
        PostDto postDto = postService.getPost(postId);
        return Helper.returnSuccessResponse("Get post success", postDto);
    }

    @PutMapping("posts/{postId}")
    public ResponseEntity<?> managePost(@PathVariable long postId, @RequestParam String action) throws Exception{
        PostDto postDto = adminService.managePost(postId,action);
        return Helper.returnSuccessResponse(action + " success", postDto);
    }

    @PutMapping("accounts/{username}")
    public ResponseEntity<?> manageAccount(@PathVariable String username, @RequestParam String action) throws Exception{
        AccountDto accountDto = adminService.manageAccount(username,action);
        return Helper.returnSuccessResponse(action + " success", accountDto);
    }

}
