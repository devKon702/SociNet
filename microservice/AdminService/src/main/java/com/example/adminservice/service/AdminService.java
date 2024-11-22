package com.example.adminservice.service;

import com.example.adminservice.dto.AccountDto;
import com.example.adminservice.dto.PostDto;
import com.example.adminservice.entity.Account;
import com.example.adminservice.entity.Post;
import com.example.adminservice.repository.AccountRepository;
import com.example.adminservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {
    private final PostRepository postRepo;
    private final AccountRepository accountRepo;

    public PostDto managePost(Long postId, String action) throws Exception {
        Optional<Post> postOpt = postRepo.findById(postId);
        if(postOpt.isEmpty()) throw new Exception("POST NOT FOUND");
        switch (action){
            case "activate":
                // update isActive = true
                postOpt.get().setActive(true);
                break;
            case "inactivate":
                // update isActive = false
                postOpt.get().setActive(false);
                break;
            default: break;
        }
        return new PostDto(postRepo.save(postOpt.get()));
    }

    public long getNumberOfPostByUserId(Long userId){
        long count = postRepo.countByUser_Id(userId);
        return count;
    }

    public List<AccountDto> getAccountsByName(String name, Pageable pageable){
        List<Account> accounts = accountRepo.findUserByNameContaining(name, "USER" , pageable);
        List<AccountDto> accountsDto = new ArrayList<>();
        accounts.forEach(account -> accountsDto.add(new AccountDto(account)));
        return accountsDto;
    }

    public AccountDto getAccount(String username) throws Exception{
        Account account = accountRepo.findAccountByUsername(username).orElseThrow(() -> new Exception("Account is not exist"));
        return new AccountDto(account);
    }

    public long getNumberOfAccountByName(String name){
        long count = accountRepo.countUserByName(name, "USER");
        return count;
    }

    public AccountDto manageAccount(String username, String action) throws Exception {
        Optional<Account> accountOpt = accountRepo.findAccountByUsername(username);
        if(accountOpt.isEmpty()) throw new Exception("Account not found");

        switch(action){
            case "activate":
                accountOpt.get().setActive(true);
                break;
            case "inactivate":
                accountOpt.get().setActive(false);
            default: break;
        }

        return new AccountDto(accountRepo.save(accountOpt.get()));
    }

}
