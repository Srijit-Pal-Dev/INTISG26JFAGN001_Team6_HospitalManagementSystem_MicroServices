package com.cognizant.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Username is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;
}
