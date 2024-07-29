package com.example.socinet.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignUpRequest {
    @NotNull(message = "Username must not be null")
    private String username;
    @NotNull(message = "Password must not be null")
    @Size(min = 6, message = "Password length must be >= 6 characters")
    private String password;
    @NotNull(message = "Name must not be null")
    @Size(max = 255, message = "Name must <= 255 characters")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "OTP must not be null")
    private String otp;
}
