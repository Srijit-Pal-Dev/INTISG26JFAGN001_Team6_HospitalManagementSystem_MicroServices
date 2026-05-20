package com.cognizant.userservice.service;

import com.cognizant.userservice.domain.PasswordResetOtp;
import com.cognizant.userservice.dto.ForgotPasswordRequest;
import com.cognizant.userservice.dto.ResetPasswordRequest;
import com.cognizant.userservice.dto.VerifyOtpRequest;
import com.cognizant.userservice.repository.PasswordResetOtpRepository;
import com.cognizant.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final JavaMailSender mailSender;

//    private void sendOtpEmail(String toEmail, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Your Password Reset OTP - PulsePoint");
//        message.setText(
//                "Dear User,\n\n" +
//                        "Your OTP for password reset is: " + otp + "\n\n" +
//                        "This OTP is valid for 5 minutes.\n" +
//                        "If you did not request this, please ignore this email.\n\n" +
//                        "Regards,\nPulsePoint Team"
//        );
//        mailSender.send(message);
//    }

    // ── Service methods ────────────────────────────────────────────────

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim();

        userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email."));

        // delete any existing unused OTPs for this email
        otpRepository.deleteByEmail(email);

        // generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetOtp entity = PasswordResetOtp.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(entity);

        // TODO: replace with real email service
        System.out.println("========================================");
        System.out.println("OTP for " + email + " : " + otp);
        System.out.println("========================================");
    }

    @Override
    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail().trim();
        String otp   = request.getOtp().trim();

        PasswordResetOtp entity = otpRepository
                .findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP."));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        // generate a short-lived reset token
        String resetToken = UUID.randomUUID().toString();
        entity.setResetToken(resetToken);
        otpRepository.save(entity);

        return resetToken;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetOtp entity = otpRepository
                .findByResetTokenAndUsedFalse(request.getResetToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired. Please start over.");
        }

        userRepository.findByUsername(entity.getEmail())
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found."));

        entity.setUsed(true);
        otpRepository.save(entity);
    }
}
