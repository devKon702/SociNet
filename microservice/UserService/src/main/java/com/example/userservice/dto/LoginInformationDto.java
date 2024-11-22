package com.example.userservice.dto;

import com.example.userservice.entity.RefreshToken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginInformationDto {
    Long id;
    String userAgent;
    String ip;
    Date createdAt;
    Date updatedAt;

    public LoginInformationDto(RefreshToken token) {
        this.id = token.getId();
        this.userAgent = token.getUserAgent();
        this.ip = token.getIp();
        this.createdAt = token.getCreatedAt();
        this.updatedAt = token.getUpdatedAt();
    }
}
