package com.example.authservice.service;

import com.example.authservice.entity.EmailOtp;
import com.example.authservice.repository.EmailOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailOtpService {
    private final JavaMailSender mailSender;
    private final EmailOtpRepository emailOtpRepo;
    @Value("${spring.mail.username}")
    private String fromMail;
    private final long EXPIRE_TIME = 5 * 60 * 1000;

    public void sendMail(String mail, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setTo(mail);

        mailSender.send(simpleMailMessage);
    }

    public boolean checkOpt(String mail, String code){
        Optional<EmailOtp> emailOtp = emailOtpRepo.findByEmailAndCode(mail, code);
        // OTP không đúng
        if(emailOtp.isEmpty()) return false;
        // OTP quá hạn
        if(emailOtp.get().getExpiredAt().getTime() < new Date().getTime()) return false;

        // Nếu đúng -> xóa Otp
        emailOtpRepo.delete(emailOtp.get());
        return true;
    }

    public void sendOtp(String mail) throws Exception{
        String otp = generateOtp(5);
        EmailOtp emailOtp = emailOtpRepo.findById(mail)
                .orElseGet(() -> {
                    return EmailOtp.builder()
                            .email(mail)
                            .build();
                });
        emailOtp.setCode(otp);
        emailOtp.setExpiredAt(new Date(System.currentTimeMillis() + EXPIRE_TIME));
        emailOtpRepo.save(emailOtp);
        String message = "This OTP will be expired after " + EXPIRE_TIME/60_000 + " minutes\n" +
                "OTP: " + otp;
        sendMail(mail, "SociNet OTP", message);
    }

    public String generateOtp(int length){
        Random random = new Random();
        String otp = "";

        for(int i = 0; i < length; i++){
            otp += Integer.toString(random.nextInt(10));
        }
        return otp;
    }


}
