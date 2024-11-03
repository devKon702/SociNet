package com.example.socinetandroid.model;

public class ApiResponse {
    private boolean isSuccess;
    private Object data;
    private String message;

    public ApiResponse(boolean isSuccess, Object data, String message) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "isSuccess=" + isSuccess +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
