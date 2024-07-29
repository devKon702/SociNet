package com.example.socinet.util;

import com.example.socinet.response.Response;
import com.example.socinet.security.AccountDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static ResponseEntity<?> returnSuccessResponse(String message, Object data){
        Response response = Response.builder()
                .isSuccess(true)
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static AccountDetail getAccountDetail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (AccountDetail) authentication.getPrincipal();
    }

    public static Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AccountDetail accountDetail = (AccountDetail) authentication.getPrincipal();
        return accountDetail.getUser().getId();
    }

}
