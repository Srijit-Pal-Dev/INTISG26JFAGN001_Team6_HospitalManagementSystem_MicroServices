package com.cognizant.userservice.mapper;

import com.cognizant.userservice.domain.Role;
import com.cognizant.userservice.domain.User;
import com.cognizant.userservice.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setRoles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()));

        return response;

    }

}
