package com.example.socinet.controller;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.dto.PostDto;
import com.example.socinet.security.AccountDetail;
import com.example.socinet.security.AccountDetailService;
import com.example.socinet.service.AdminService;
import com.example.socinet.service.PostService;
import com.example.socinet.util.Helper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final PostService postService;

    @GetMapping("posts")
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<PostDto> posts = postService.getPosts(page,size);
        return Helper.returnSuccessResponse("Get post list success", posts);
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

    @GetMapping("accounts")
    public ResponseEntity<?> getAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<AccountDto> accountsDto = adminService.getAccounts(page, size);
        return Helper.returnSuccessResponse("Get user list success", accountsDto);
    }

    @PutMapping("accounts/{username}")
    public ResponseEntity<?> manageAccount(@PathVariable String username, @RequestParam String action) throws Exception{
        AccountDto accountDto = adminService.manageAccount(username,action);
        return Helper.returnSuccessResponse(action + " success", accountDto);
    }

}
