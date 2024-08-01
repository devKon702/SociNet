package com.example.socinet.service;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.entity.Account;
import com.example.socinet.repository.AccountRepository;
import com.example.socinet.security.AccountDetail;
import com.example.socinet.util.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailOtpService emailOtpService;

    public AccountDto getAccount(){
        return new AccountDto(Helper.getAccountDetail());
    }

    public void changePassword(String oldPassword, String newPassword) throws Exception {
        AccountDetail accountDetail = Helper.getAccountDetail();
        if(passwordEncoder.matches(oldPassword, accountDetail.getPassword())){
            Optional<Account> accountOpt = accountRepo.findAccountByUsername(accountDetail.getUsername());
            if(accountOpt.isPresent()){
                accountOpt.get().setPassword(passwordEncoder.encode(newPassword));
                accountRepo.save(accountOpt.get());
            }
        } else{
            throw new Exception("Password is incorrect");
        }
    }

    public AccountDto changeEmail(String newEmail, String otp) throws Exception {
        // Kiểm tra email trùng
        if(accountRepo.existsByEmail(newEmail))
            throw new Exception("Email has already been used");
        if(!emailOtpService.checkOpt(newEmail, otp))
            throw new Exception("OTP invalid");
        // Thỏa mãn điều kiện
        AccountDetail accountDetail = Helper.getAccountDetail();
        Optional<Account> accountOpt = accountRepo.findAccountByUsername(accountDetail.getUsername());
        if(accountOpt.isPresent()){
            accountOpt.get().setEmail(newEmail);
            return new AccountDto(accountRepo.save(accountOpt.get()));
        } else throw new Exception("Account not exist");

    }
}
