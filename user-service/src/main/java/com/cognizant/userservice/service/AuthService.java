package com.cognizant.userservice.service;

import com.cognizant.userservice.domain.RefreshToken;
import com.cognizant.userservice.dto.LoginRequest;
import com.cognizant.userservice.dto.RefreshTokenRequest;
import com.cognizant.userservice.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {

    void logout(String refreshToken);

    RefreshToken createRefreshToken(String username);

    RefreshToken verifyExpiration(RefreshToken token);

    Map<String, String> login(LoginRequest request);

    Map<String, String> refreshAccessToken(RefreshTokenRequest request);

    Map<String, String> register(RegisterRequest request);

}
