package com.cognizant.userservice.controller;

import com.cognizant.userservice.domain.*;
import com.cognizant.userservice.dto.*;
import com.cognizant.userservice.exception.InvalidRoleException;
import com.cognizant.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and token generation")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive a JWT token")
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));

    }

    @PostMapping("/refresh")
    @Transactional
    @Operation(summary = "Refresh JWT Token", description = "Refresh access Token")
    public ResponseEntity<Map<String,String>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshAccessToken(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register User", description = "A perosn using the webApp can register as a User")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/logout")
    @Transactional
    @Operation(summary = "User Logout", description = "User can log out from webApp")
    public ResponseEntity<Map<String,String>> logout(
            @RequestHeader("X-User-Role") String roles,
            @Valid @RequestBody LogoutRequest request) {

        if(!roles.contains("ADMIN") && !roles.contains("DOCTOR") && !roles.contains("USER") && !roles.contains("PHARMACIST") && !roles.contains("LAB_TECHNICIAN")){
            throw new InvalidRoleException("Invalid Access");
        }

        authService.logout(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Successfully logged out"));
    }

}
