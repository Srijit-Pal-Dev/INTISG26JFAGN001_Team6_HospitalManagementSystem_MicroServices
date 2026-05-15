package com.cognizant.userservice.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String resetToken;

    private String newPassword;
}