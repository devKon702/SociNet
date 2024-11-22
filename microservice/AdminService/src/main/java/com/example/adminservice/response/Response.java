package com.example.adminservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Response {
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private String message;
    private Object data;
}
