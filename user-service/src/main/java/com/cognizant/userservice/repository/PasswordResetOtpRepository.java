package com.cognizant.userservice.repository;

import com.cognizant.userservice.domain.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmailAndOtpAndUsedFalse(String email, String otp);

    Optional<PasswordResetOtp> findByResetTokenAndUsedFalse(String resetToken);

    void deleteByEmail(String email);
}