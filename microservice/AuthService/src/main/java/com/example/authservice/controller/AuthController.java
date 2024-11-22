package com.example.authservice.controller;

import com.example.authservice.dto.AccountDto;
import com.example.authservice.dto.AuthDto;
import com.example.authservice.request.SignInRequest;
import com.example.authservice.request.SignUpRequest;
import com.example.authservice.response.Response;
import com.example.authservice.service.AuthService;
import com.example.authservice.util.Helper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


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
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest body,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestParam(required = false) String ip) throws Exception{
        String userAgent = request.getHeader("User-Agent");
        if(ip == null){
            ip = request.getHeader("X-FORWARED-FOR");
            if(ip == null || ip.isEmpty()){
                ip = request.getRemoteAddr();
            }
        }
        AuthDto authDto = authService.signIn(body, userAgent, ip);
        response.addCookie(Helper.createCookie("refreshToken", authDto.getRefreshToken(), 30 * 24 * 60 * 60, "/api/v1/auth/refresh-token"));
        return Helper.returnSuccessResponse("Sign in success", authDto);
    }

    @PostMapping("google")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String email,
                                             @RequestParam String googleId,
                                             @RequestParam String name,
                                             @RequestParam String avatarUrl,
                                             @RequestParam(required = false) String ip,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        String userAgent = request.getHeader("User-Agent");
        if(ip == null){
            ip = request.getHeader("X-FORWARED-FOR");
            if(ip == null || ip.isEmpty()){
                ip = request.getRemoteAddr();
            }
        }
        AuthDto authDto = authService.signInWithGoogle(email, name, avatarUrl, googleId, userAgent, ip);
        response.addCookie(Helper.createCookie("refreshToken", authDto.getRefreshToken(), 30 * 24 * 60 * 60, "/api/v1/auth/refresh-token"));
        return Helper.returnSuccessResponse("Login with google success", authDto);
    }

    @GetMapping("refresh-token")
    public ResponseEntity<?> doRefreshToken(HttpServletRequest request,
                                            HttpServletResponse response) throws Exception, ExpiredJwtException {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        AuthDto authDto = authService.doRefreshToken(refreshToken);
        response.addCookie(Helper.createCookie("refreshToken", authDto.getRefreshToken(), 30 * 24 * 60 * 60, "/api/v1/auth/refresh-token"));
        return Helper.returnSuccessResponse("Refresh token success", authDto);
    }

    @GetMapping("otp/{email}")
    public ResponseEntity<?> sendOtp(@PathVariable String email) throws Exception{
        authService.sendOtp(email);
        return Helper.returnSuccessResponse("OTP has been sent", null);
    }

    @GetMapping("forgot-password")
    public ResponseEntity<?> searchByEmail(@RequestParam String email) throws Exception{
        AccountDto accountDto = authService.searchByEmail(email);
        return Helper.returnSuccessResponse("Search account success", accountDto);
    }

    @PutMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email,
                                            @RequestParam String newPassword,
                                            @RequestParam String otp) throws Exception{
        AccountDto accountDto = authService.forgotPassword(email, newPassword, otp);
        return Helper.returnSuccessResponse("Retrieve account success", accountDto);
    }
}
