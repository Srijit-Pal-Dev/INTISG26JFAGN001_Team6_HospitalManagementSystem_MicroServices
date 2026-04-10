package com.cognizant.userservice.service;

import com.cognizant.userservice.dto.CreateUserRequest;
import com.cognizant.userservice.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request, String role);

    UserResponse getUserById(Long id, String role);

    UserResponse getUserByUsername(String username, String role);

    void deleteUser(Long id, String role);

    List<UserResponse> getAllUsers(String role);

}
