package com.example.authservice.dto;

import com.example.authservice.entity.Account;
import com.example.authservice.security.AccountDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class AccountDto {
    String username;
    String email;
    @JsonProperty("isEmailAuth")
    boolean isEmailAuth;
    @JsonProperty("isActive")
    boolean isActive;
    Set<String> roles;
    UserDto user;
    public AccountDto(Account account){
        if(account == null) return;
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.isEmailAuth = account.isEmailAuth();
        this.isActive = account.isActive();
        this.roles = new HashSet<>();
        account.getRoles().stream().forEach(role -> roles.add(role.getRole()));
        user = new UserDto(account.getUser());
    }

    public AccountDto(AccountDetail accountDetail){
        if(accountDetail == null) return;
        this.username = accountDetail.getUsername();
        this.email = accountDetail.getEmail();
        this.isActive = accountDetail.isActive();
        this.roles = new HashSet<>();
        this.user = new UserDto(accountDetail.getUser());
    }
}
