package com.example.socinet.service;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.dto.PostDto;
import com.example.socinet.entity.Account;
import com.example.socinet.entity.Post;
import com.example.socinet.repository.AccountRepository;
import com.example.socinet.repository.PostRepository;
import com.example.socinet.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    public PostDto managePost(Long postId, String action) throws Exception {
        Optional<Post> postOpt = postRepo.findById(postId);
        if(postOpt.isEmpty()) throw new Exception("Post not found");
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

    public List<AccountDto> getAccounts(int page, int size){
        List<Account> accounts = accountRepo.findAll();
        List<AccountDto> accountsDto = new ArrayList<>();
        accounts.forEach(account -> accountsDto.add(new AccountDto(account)));
        return accountsDto;
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
