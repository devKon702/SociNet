package com.example.socinet.controller;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.dto.LoginInformationDto;
import com.example.socinet.service.AccountService;
import com.example.socinet.util.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @GetMapping("")
    public ResponseEntity<?> getAccount(){
        AccountDto accountDto = accountService.getAccount();
        return Helper.returnSuccessResponse("Get account info success", accountDto);
    }

    @PutMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) throws Exception{
        accountService.changePassword(oldPassword, newPassword);
        return Helper.returnSuccessResponse("Change password success", null);
    }

    @PutMapping("change-email")
    public ResponseEntity<?> changeEmail(@RequestParam String newEmail, @RequestParam String otp) throws Exception{
        accountService.changeEmail(newEmail, otp);
        return Helper.returnSuccessResponse("Change email success", null);
    }

    @GetMapping("login-session")
    public ResponseEntity<?> getLoginSession(){
        List<LoginInformationDto> result = accountService.getLoginInformationList();
        return Helper.returnSuccessResponse("Get login information list success", result);
    }

    @DeleteMapping("login-session")
    public ResponseEntity<?> removeLoginSession(@RequestParam Long id){
        accountService.removeDevice(id);
        return Helper.returnSuccessResponse("Remove device success", null);
    }
}
