package com.example.socinet.controller;

import com.example.socinet.service.EmailOtpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
@AllArgsConstructor
public class TestController {
    private final EmailOtpService emailOtpService;
    @GetMapping("")
    public String test(HttpServletRequest request){
        String text = request.getHeader("X-FORWARED-FOR");
        return "Test response from API" + text;
    }

    @GetMapping("test-mail")
    public String testMail() throws Exception{
        emailOtpService.sendOtp("nhatkha117@gmail.com");
        return "Send email success";
    }
}
