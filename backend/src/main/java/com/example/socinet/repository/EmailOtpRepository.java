package com.example.socinet.repository;

import com.example.socinet.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, String> {
    Optional<EmailOtp> findByEmailAndCode(String mail, String code);
}
