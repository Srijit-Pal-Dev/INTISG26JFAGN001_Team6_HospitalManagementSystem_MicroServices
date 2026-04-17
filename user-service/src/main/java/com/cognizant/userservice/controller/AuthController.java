package com.cognizant.userservice.controller;

import com.cognizant.userservice.domain.*;
import com.cognizant.userservice.dto.*;
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
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Login Successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Login Response",
                                                    value = "{\n" +
                                                            "  \"accessToken\": \"<access token...>\",\n" +
                                                            "  \"refreshToken\": \"<refresh token...>\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Invalid Credentials Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 401,\n" +
                                                            "  \"error\": \"Unauthorized\",\n" +
                                                            "  \"message\": \"Invalid username or password\",\n" +
                                                            "  \"path\": \"/auth/login\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 400,\n" +
                                                            "  \"error\": \"Bad Request\",\n" +
                                                            "  \"message\": \"Validation failed for object='loginRequest'. Error count: 1\",\n" +
                                                            "  \"fieldErrors\": {\n" +
                                                            "    \"username\": \"Username must not be blank\"\n" +
                                                            "  },\n" +
                                                            "  \"path\": \"/auth/login\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )

            }
    )
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));

    }

    @PostMapping("/refresh")
    @Transactional
    @Operation(summary = "Refresh JWT Token", description = "Refresh access Token")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Token refreshed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Token Refresh Response",
                                                    value = "{\n" +
                                                            "  \"accessToken\": \"<new access token...>\",\n" +
                                                            "  \"refreshToken\": \"<new refresh token...>\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid or expired refresh token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Invalid Refresh Token Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 401,\n" +
                                                            "  \"error\": \"Unauthorized\",\n" +
                                                            "  \"message\": \"Invalid or expired refresh token\",\n" +
                                                            "  \"path\": \"/auth/refresh\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 400,\n" +
                                                            "  \"error\": \"Bad Request\",\n" +
                                                            "  \"message\": \"Validation failed for object='refreshTokenRequest'. Error count: 1\",\n" +
                                                            "  \"fieldErrors\": {\n" +
                                                            "    \"refreshToken\": \"Refresh token must not be blank\"\n" +
                                                            "  },\n" +
                                                            "  \"path\": \"/auth/refresh\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )

            }
    )
    public ResponseEntity<Map<String,String>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(authService.refreshAccessToken(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register patient", description = "A perosn using the webApp can register as a patient")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Registration successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Registration Response",
                                                    value = "{\n" +
                                                            "  \"message\": \"User registered successfully\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Validation errors or username already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 400,\n" +
                                                            "  \"error\": \"Bad Request\",\n" +
                                                            "  \"message\": \"Validation failed for object='registerRequest'. Error count: 1\",\n" +
                                                            "  \"fieldErrors\": {\n" +
                                                            "    \"username\": \"Username must not be blank\"\n" +
                                                            "  },\n" +
                                                            "  \"path\": \"/auth/register\"\n" +
                                                            "}"
                                            ),
                                            @ExampleObject(
                                                    name = "Username Already Exists Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 400,\n" +
                                                            "  \"error\": \"Bad Request\",\n" +
                                                            "  \"message\": \"Username already exists\",\n" +
                                                            "  \"path\": \"/auth/register\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )

            }
    )
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/logout")
    @Transactional
    @Operation(summary = "User Logout", description = "User can log out from webApp")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Logout Response",
                                                    value = "{\n" +
                                                            "  \"message\": \"Successfully logged out\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation Error Response",
                                                    value = "{\n" +
                                                            "  \"timestamp\": \"2024-06-01T12:00:00Z\",\n" +
                                                            "  \"status\": 400,\n" +
                                                            "  \"error\": \"Bad Request\",\n" +
                                                            "  \"message\": \"Validation failed for object='logoutRequest'. Error count: 1\",\n" +
                                                            "  \"fieldErrors\": {\n" +
                                                            "    \"refreshToken\": \"Refresh token must not be blank\"\n" +
                                                            "  },\n" +
                                                            "  \"path\": \"/auth/logout\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )

            }
    )
    public ResponseEntity<Map<String,String>> logout(@Valid @RequestBody LogoutRequest request) {

        authService.logout(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Successfully logged out"));
    }

}
