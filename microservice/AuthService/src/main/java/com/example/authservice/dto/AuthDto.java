package com.example.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthDto {
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
    private AccountDto account;
    private Long loginSessionId;
}
