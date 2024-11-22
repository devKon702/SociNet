package com.example.authservice.service;

import com.example.authservice.dto.AccountDto;
import com.example.authservice.dto.LoginInformationDto;
import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.repository.AccountRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.security.AccountDetail;
import com.example.authservice.util.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailOtpService emailOtpService;
    private final RefreshTokenRepository refreshTokenRepository;

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
            throw new Exception("USED EMAIL");
        if(!emailOtpService.checkOpt(newEmail, otp))
            throw new Exception("INVALID OTP");
        // Thỏa mãn điều kiện
        AccountDetail accountDetail = Helper.getAccountDetail();
        Optional<Account> accountOpt = accountRepo.findAccountByUsername(accountDetail.getUsername());
        if(accountOpt.isPresent()){
            accountOpt.get().setEmail(newEmail);
            return new AccountDto(accountRepo.save(accountOpt.get()));
        } else throw new Exception("Account not exist");
    }

    public List<LoginInformationDto> getLoginInformationList(){
        List<RefreshToken> mLoginList = refreshTokenRepository.findByAccount_Username(Helper.getAccountDetail().getUsername());
        List<LoginInformationDto> mLoginListDto = new ArrayList<>();
        mLoginList.forEach((item) -> {
            mLoginListDto.add(new LoginInformationDto(item));
        });

        return mLoginListDto;
    }

    public void removeDevice(Long id) {
        refreshTokenRepository.deleteById(id);
    }
}
