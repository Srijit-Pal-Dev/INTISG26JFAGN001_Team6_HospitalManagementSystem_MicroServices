package com.cognizant.userservice.controller;

import com.cognizant.userservice.dto.CreateUserRequest;
import com.cognizant.userservice.dto.UserResponse;
import com.cognizant.userservice.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing hospital staff users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new User", description = "Registers a new hospital staff member. ADMIN only.")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Creation",
                                                    value = "{\n" +
                                                            "  \"message\": \"Successfully User Created\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Only ADMIN can create users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Forbidden Access",
                                                    value = "{\n" +
                                                            "  \"message\": \"Access denied: Only ADMIN can create users\"\n" +
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
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                name = "Validation Error",
                                                value = "{\n" +
                                                        "  \"message\": \"Validation failed\",\n" +
                                                        "  \"errors\": [\n" +
                                                        "    \"Username is required\",\n" +
                                                        "    \"Password must be at least 8 characters\"\n" +
                                                        "  ]\n" +
                                                        "}"
                                        )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request,role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Retrieval",
                                                    value = "{\n" +
                                                            "  \"id\": 1,\n" +
                                                            "  \"username\": \"jdoe\",\n" +
                                                            "  \"fullName\": \"John Doe\",\n" +
                                                            "  \"roles\": [\"DOCTOR\"]\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Forbidden Access",
                                                    value = "{\n" +
                                                            "  \"message\": \"Access denied: Insufficient permissions\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "User Not Found",
                                                    value = "{\n" +
                                                            "  \"message\": \"User with ID 1 not found\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id,role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by Usernname")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Retrieval",
                                                    value = "{\n" +
                                                            "  \"id\": 1,\n" +
                                                            "  \"username\": \"jdoe\",\n" +
                                                            "  \"fullName\": \"John Doe\",\n" +
                                                            "  \"roles\": [\"DOCTOR\"]\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Forbidden Access",
                                                    value = "{\n" +
                                                            "  \"message\": \"Access denied: Insufficient permissions\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "User Not Found",
                                                    value = "{\n" +
                                                            "  \"message\": \"User with username is not found\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<UserResponse> getUserByUsername(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String username) {

        UserResponse response = userService.getUserByUsername(username,role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Show all users", description = "Returns all registered staff. ADMIN only.")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Users retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Retrieval",
                                                    value = "[\n" +
                                                            "  {\n" +
                                                            "    \"id\": 1,\n" +
                                                            "    \"username\": \"jdoe\",\n" +
                                                            "    \"fullName\": \"John Doe\",\n" +
                                                            "    \"roles\": [\"DOCTOR\"]\n" +
                                                            "  },\n" +
                                                            "  {\n" +
                                                            "    \"id\": 2,\n" +
                                                            "    \"username\": \"asmith\",\n" +
                                                            "    \"fullName\": \"Alice Smith\",\n" +
                                                            "    \"roles\": [\"NURSE\"]\n" +
                                                            "  }\n" +
                                                            "]"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Only ADMIN can view all users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Forbidden Access",
                                                    value = "{\n" +
                                                            "  \"message\": \"Access denied: Only ADMIN can view all users\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public  ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader("X-User-Role") String role) {

        List<UserResponse> response = userService.getAllUsers(role);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete an user by Id", description = "Permanently removes a user. ADMIN only.")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Successful Deletion",
                                                    value = "{\n" +
                                                            "  \"message\": \"User with ID 1 deleted successfully\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Only ADMIN can delete users",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Forbidden Access",
                                                    value = "{\n" +
                                                            "  \"message\": \"Access denied: Only ADMIN can delete users\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Not Found - User does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "User Not Found",
                                                    value = "{\n" +
                                                            "  \"message\": \"User with ID 1 not found\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        userService.deleteUser(id,role);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
