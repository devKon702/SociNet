package com.example.socinet.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInRequest {
    @NotNull(message = "Username must not be null")
    String username;
    @NotNull(message = "Password must not be null")
    String password;
}
