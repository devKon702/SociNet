package com.example.socinet.service;

import com.example.socinet.dto.AccountDto;
import com.example.socinet.dto.AuthDto;
import com.example.socinet.entity.Account;
import com.example.socinet.entity.RefreshToken;
import com.example.socinet.entity.Role;
import com.example.socinet.entity.User;
import com.example.socinet.jwt.JwtProvider;
import com.example.socinet.repository.AccountRepository;
import com.example.socinet.repository.RefreshTokenRepository;
import com.example.socinet.repository.UserRepository;
import com.example.socinet.request.SignInRequest;
import com.example.socinet.request.SignUpRequest;
import com.example.socinet.response.Response;
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
            throw new Exception("Username has been used");
        }
        // Kiểm tra độ dài password
        if(signUpRequest.getPassword().length() < 6){
            throw new Exception("Password must > 6 characters");
        }
        // Kiểm tra email có trùng không
        if(accountRepo.existsByEmail(signUpRequest.getEmail())){
            throw new Exception("Email has bean used");
        }
        // Kiểm tra OTP
        if(!emailOtpService.checkOpt(signUpRequest.getEmail(), signUpRequest.getOtp())){
            throw new Exception("Invalid OTP");
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

    public AuthDto signIn(SignInRequest signInRequest) throws Exception{
        // Xác thực
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()));
        Optional<Account> account = accountRepo.findAccountByUsername(signInRequest.getUsername());
        // Xác thực thành công
        if(account.isPresent() && authentication.isAuthenticated()){
            AccountDto accountDto = new AccountDto(account.get());
            String accessToken = jwtProvider.generateAccessToken(accountDto.getUsername());
            String refreshToken = jwtProvider.generateRefreshToken(accountDto.getUsername());
            // Kiểm tra đã có thiết bị đăng nhập chưa = tài khoản đã lưu một refresh token chưa
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByAccount_Username(accountDto.getUsername());
            if(refreshTokenOpt.isPresent()){
                // Cập nhật
                refreshTokenOpt.get().setToken(refreshToken);
                refreshTokenRepo.save(refreshTokenOpt.get());
            } else{
                // Tạo mới
                RefreshToken newRefreshToken = RefreshToken.builder()
                        .account(account.get())
                        .token(refreshToken)
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
            throw new Exception("Authentication failed");
        }
    }

    public AuthDto doRefreshToken(String refreshToken) throws ExpiredJwtException, Exception {
        if(refreshToken != null && jwtProvider.validateToken(refreshToken, true)){
            // Kiểm tra subject trong token hợp lệ
            String username = jwtProvider.getSubjectFromJwt(refreshToken, true);
            Optional<Account> accountOpt = accountRepo.findAccountByUsername(username);
            if(accountOpt.isEmpty()) throw new Exception("Not found account " + username);

            // Kiểm tra token có lưu dưới db
            Optional<RefreshToken> savedTokenOpt = refreshTokenRepo.findByAccount_UsernameAndToken(username, refreshToken);

            // Nếu không -> throw lỗi
            if(savedTokenOpt.isEmpty()) throw new Exception("Unauthenticated");
            // Nếu có -> generate token mới và cập nhật
            String newRefreshToken = jwtProvider.generateRefreshToken(username);
            String newAccessToken = jwtProvider.generateAccessToken(username);
            savedTokenOpt.get().setToken(newRefreshToken);
            refreshTokenRepo.save(savedTokenOpt.get());
            return AuthDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .account(new AccountDto(accountOpt.get()))
                    .build();
        } else{
            throw new Exception("Refresh token is null or invalid");
        }
    }

    public void sendOtp(String email) throws Exception{
        emailOtpService.sendOtp(email);
    }

    public AccountDto forgotPassword(String email, String newPassword, String otp) throws Exception{
        // Kiểm tra OTP hợp lệ
        if(!emailOtpService.checkOpt(email, otp)) throw new Exception("Invalid OTP");
        // Kiểm tra độ dài password
        if(newPassword.length() < 6) throw new Exception("Password must be >= 6 characters");
        Optional<Account> accountOpt = accountRepo.findByEmail(email);
        if(accountOpt.isPresent()){
            accountOpt.get().setPassword(passwordEncoder.encode(newPassword));
            return new AccountDto(accountRepo.save(accountOpt.get()));
        } else{
            throw new Exception("Not found account with email: " + email);
        }
    }
}