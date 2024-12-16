package com.example.authservice.service;

import com.example.authservice.dto.AccountDto;
import com.example.authservice.dto.AuthDto;
import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.User;
import com.example.authservice.jwt.JwtProvider;
import com.example.authservice.repository.AccountRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.request.SignInRequest;
import com.example.authservice.request.SignUpRequest;
import com.example.authservice.response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtProvider jwtProvider;
    private final EmailOtpService emailOtpService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Response signUp(SignUpRequest signUpRequest) throws Exception {
        // Kiểm tra username có trùng không
        if(accountRepo.existsByUsername(signUpRequest.getUsername())){
            throw new Exception("USED USERNAME");
        }
        // Kiểm tra độ dài password
        if(signUpRequest.getPassword().length() < 6){
            throw new Exception("INVALID PASSWORD");
        }
        // Kiểm tra email có trùng không
        if(accountRepo.existsByEmail(signUpRequest.getEmail())){
            throw new Exception("USED EMAIL");
        }

        // Kiểm tra OTP
        if(!emailOtpService.checkOpt(signUpRequest.getEmail(), signUpRequest.getOtp())){
            throw new Exception("INVALID OTP");
        }

        // Build user
        User user = User.builder()
                .name(signUpRequest.getName())
                .isMale(true)
                .build();
        // Build account
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("USER"));
        Account account = Account.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .email(signUpRequest.getEmail())
                .isActive(true)
                .roles(roles)
                .isEmailAuth(false)
                .user(userRepo.save(user))
                .build();

        AccountDto accountDto = new AccountDto(accountRepo.save(account));
        return Response.builder()
                .isSuccess(true)
                .message("Sign up success")
                .data(accountDto)
                .build();
    }

    public AuthDto signIn(SignInRequest signInRequest, String userAgent, String ip) throws Exception{
        // Xác thực
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()));
        Optional<Account> account = accountRepo.findAccountByUsername(signInRequest.getUsername());
        if(account.isEmpty()) throw new Exception("ACCOUNT NOT FOUND");
        // Xác thực thành công
        if(authentication.isAuthenticated()){
            AccountDto accountDto = new AccountDto(account.get());
            // Kiểm tra tài khoản có active không
            if(!accountDto.isActive()) throw new Exception("INACTIVE ACCOUNT");
            String accessToken = jwtProvider.generateAccessToken(accountDto.getUsername());
            String refreshToken = jwtProvider.generateRefreshToken(accountDto.getUsername());
            // Kiểm tra đã có thiết bị đăng nhập chưa = tài khoản đã lưu một refresh token chưa
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByAccount_UsernameAndIp(accountDto.getUsername(), ip);
            if(refreshTokenOpt.isPresent()){
                // Cập nhật
                refreshTokenOpt.get().setToken(refreshToken);
                refreshTokenOpt.get().setUserAgent(userAgent);
                refreshTokenRepo.save(refreshTokenOpt.get());
            } else{
                // Tạo mới
                RefreshToken newRefreshToken = RefreshToken.builder()
                        .account(account.get())
                        .token(refreshToken)
                        .ip(ip)
                        .userAgent(userAgent)
                        .build();
                refreshTokenRepo.save(newRefreshToken);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return AuthDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .account(accountDto)
                    .build();
        } else{
            throw new Exception("INCORRECT PASSWORD");
        }
    }

    public AuthDto signInWithGoogle(String email, String name, String avatarUrl, String googleId, String userAgent, String ip) throws Exception {
        Account account = accountRepo.findByEmail(email).orElseGet(() -> {
            // Build user
            User newUser = User.builder()
                    .name(name)
                    .isMale(true)
                    .avatarUrl(avatarUrl)
                    .build();
            // Build account
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("USER"));
            Account newAccount = Account.builder()
                    .username(googleId)
                    .password(passwordEncoder.encode(googleId))
                    .email(email)
                    .isActive(true)
                    .roles(roles)
                    .isEmailAuth(true)
                    .user(userRepo.save(newUser))
                    .build();
            return accountRepo.save(newAccount);
        });

        String accessToken = jwtProvider.generateAccessToken(account.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(account.getUsername());
        // Kiểm tra đã có thiết bị đăng nhập chưa = tài khoản đã lưu một refresh token chưa
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByAccount_UsernameAndIp(account.getUsername(), ip);
        if(refreshTokenOpt.isPresent()){
            // Cập nhật
            refreshTokenOpt.get().setToken(refreshToken);
            refreshTokenOpt.get().setUserAgent(userAgent);
            refreshTokenRepo.save(refreshTokenOpt.get());
        } else{
            // Tạo mới
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .account(account)
                    .token(refreshToken)
                    .ip(ip)
                    .userAgent(userAgent)
                    .build();
            refreshTokenRepo.save(newRefreshToken);
        }

        return AuthDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .account(new AccountDto(account))
                .build();
    }

    public AuthDto doRefreshToken(String refreshToken, String ip, String userAgent) throws ExpiredJwtException, Exception {
        if(refreshToken != null && jwtProvider.validateToken(refreshToken, true)){
            // Kiểm tra subject trong token hợp lệ
            String username = jwtProvider.getSubjectFromJwt(refreshToken, true);
            Optional<Account> accountOpt = accountRepo.findAccountByUsername(username);
            if(accountOpt.isEmpty()) throw new Exception("Not found account " + username);

            // Kiểm tra token có lưu dưới db
            Optional<RefreshToken> savedTokenOpt = refreshTokenRepo.findByAccount_UsernameAndToken(username, refreshToken);
            // Nếu không -> throw lỗi
            if(savedTokenOpt.isEmpty()) throw new Exception("Unauthenticated");
            // Nếu có -> Kiểm tra user agent có giống không?
            if(!savedTokenOpt.get().getUserAgent().equals(userAgent)) throw new Exception("STRANGE DEVICE");
            // Nếu giống
            String newRefreshToken = jwtProvider.generateRefreshToken(username);
            String newAccessToken = jwtProvider.generateAccessToken(username);
            savedTokenOpt.get().setToken(newRefreshToken);
            if(ip != null && !ip.isEmpty() && !savedTokenOpt.get().getIp().equals(ip)) {
                savedTokenOpt.get().setIp(ip);
            }
            refreshTokenRepo.save(savedTokenOpt.get());
            return AuthDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .account(new AccountDto(accountOpt.get()))
                    .build();
        } else{
            throw new Exception("INVALID TOKEN");
        }
    }

    public void sendOtp(String email) throws Exception{
        emailOtpService.sendOtp(email);
    }

    public AccountDto searchByEmail(String email) throws Exception{
        Optional<Account> account = accountRepo.findByEmail(email);
        if(account.isPresent()) return new AccountDto(account.get());
        else return null;
    }

    public AccountDto forgotPassword(String email, String newPassword, String otp) throws Exception{
        // Kiểm tra OTP hợp lệ
        if(!emailOtpService.checkOpt(email, otp)) throw new Exception("INVALID OTP");
        // Kiểm tra độ dài password
        if(newPassword.length() < 6) throw new Exception("TOO SHORT PASSWORD");
        Optional<Account> accountOpt = accountRepo.findByEmail(email);
        if(accountOpt.isPresent()){
            accountOpt.get().setPassword(passwordEncoder.encode(newPassword));
            return new AccountDto(accountRepo.save(accountOpt.get()));
        } else{
            throw new Exception("ACCOUNT NOT FOUND");
        }
    }
}
