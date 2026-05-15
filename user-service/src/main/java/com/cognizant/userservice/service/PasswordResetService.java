package com.cognizant.userservice.service;

import com.cognizant.userservice.dto.ForgotPasswordRequest;
import com.cognizant.userservice.dto.ResetPasswordRequest;
import com.cognizant.userservice.dto.VerifyOtpRequest;

public interface PasswordResetService {

    void forgotPassword(ForgotPasswordRequest request);

    String verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);
}
