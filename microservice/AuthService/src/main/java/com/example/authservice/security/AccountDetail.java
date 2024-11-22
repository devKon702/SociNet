package com.example.authservice.security;

import com.example.authservice.entity.Account;
import com.example.authservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class AccountDetail implements UserDetails {
    private String username;
    private String password;
    private String email;
    private boolean isEmailAuth;
    private Collection<? extends GrantedAuthority> authorities;
    private User user;
    private boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public static AccountDetail mapAccountToAccountDetail(Account account){
        List<GrantedAuthority> listAuthorities = account.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
        return AccountDetail.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .email(account.getEmail())
                .user(account.getUser())
                .isEmailAuth(account.isEmailAuth())
                .authorities(listAuthorities)
                .isActive(account.isActive())
                .build();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
