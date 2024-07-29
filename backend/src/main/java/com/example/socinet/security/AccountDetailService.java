package com.example.socinet.security;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.entity.Account;
import com.example.socinet.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@AllArgsConstructor
public class AccountDetailService implements UserDetailsService {
    private final AccountRepository accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findAccountByUsername(username);
        if(account.isPresent()){
            return AccountDetail.mapAccountToAccountDetail(account.get());
        }
        else {
            throw new UsernameNotFoundException("Account not found");
        }
    }

}
