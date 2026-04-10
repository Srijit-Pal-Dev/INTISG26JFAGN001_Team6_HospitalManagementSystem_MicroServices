package com.cognizant.userservice.controller;

import com.cognizant.userservice.dto.CreateUserRequest;
import com.cognizant.userservice.dto.UserResponse;
import com.cognizant.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing hospital staff users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new User", description = "Registers a new hospital staff member. ADMIN only.")
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request,role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id,role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by Usernname")
    public ResponseEntity<UserResponse> getUserByUsername(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String username) {

        UserResponse response = userService.getUserByUsername(username,role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Show all users", description = "Returns all registered staff. ADMIN only.")
    public  ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader("X-User-Role") String role) {

        List<UserResponse> response = userService.getAllUsers(role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete an user by Id", description = "Permanently removes a user. ADMIN only.")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        userService.deleteUser(id,role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
