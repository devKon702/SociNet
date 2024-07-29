package com.example.socinet.controller;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.dto.AuthDto;
import com.example.socinet.request.SignInRequest;
import com.example.socinet.request.SignUpRequest;
import com.example.socinet.response.Response;
import com.example.socinet.service.AuthService;
import com.example.socinet.util.Helper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest body) throws Exception {
        Response response = authService.signUp(body);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest body, HttpServletRequest request) throws Exception{
        log.info(request.getRemoteAddr());
        AuthDto authDto = authService.signIn(body);
        return Helper.returnSuccessResponse("Sign in success", authDto);
    }

    @GetMapping("refresh-token/{token}")
    public ResponseEntity<?> doRefreshToken(@PathVariable String token) throws Exception, ExpiredJwtException {
        AuthDto authDto = authService.doRefreshToken(token);
        return Helper.returnSuccessResponse("Refresh token success", authDto);
    }

    @GetMapping("otp/{email}")
    public ResponseEntity<?> sendOtp(@PathVariable String email) throws Exception{
        authService.sendOtp(email);
        return Helper.returnSuccessResponse("OTP has been sent", null);
    }

    @PutMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email,
                                            @RequestParam String newPassword,
                                            @RequestParam String otp) throws Exception{
        AccountDto accountDto = authService.forgotPassword(email, newPassword, otp);
        return Helper.returnSuccessResponse("Retrieve account success", accountDto);
    }
}
