package com.cognizant.userservice.service;

import com.cognizant.userservice.client.NotificationClient;
import com.cognizant.userservice.domain.NotificationType;
import com.cognizant.userservice.domain.Role;
import com.cognizant.userservice.domain.User;
import com.cognizant.userservice.dto.CreateUserRequest;
import com.cognizant.userservice.dto.SendNotificationRequest;
import com.cognizant.userservice.dto.UserResponse;
import com.cognizant.userservice.exception.InvalidRoleException;
import com.cognizant.userservice.exception.UserNotFoundException;
import com.cognizant.userservice.mapper.UserMapper;
import com.cognizant.userservice.repository.RoleRepository;
import com.cognizant.userservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    @CircuitBreaker(name="myCircuitBreaker", fallbackMethod = "fallback")
    public UserResponse createUser(CreateUserRequest request, String role) {

        if (!role.contains("ADMIN") && !role.contains("USER")) {
            throw new InvalidRoleException("Forbidden Access");
        }

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Set<Role> roles = new HashSet<>();
        for(var roleName : request.getRoles()) {
            Role check = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found : "+roleName));
            roles.add(check);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEnabled(true);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setUserId(user.getId());
        sendNotificationRequest.setTitle("User Created");
        sendNotificationRequest.setMessage(roles+" "+user.getFullName()+" has been Created");
        sendNotificationRequest.setType(NotificationType.GENERAL);

        notificationClient.send(sendNotificationRequest);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse getUserById(Long id, String role) {

        if (!role.contains("ADMIN")) {
            throw new InvalidRoleException("Forbidden Access");
        }
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id : "+id)));
    }

    @Override
    @Transactional
    public UserResponse getUserByUsername(String username, String role) {

        if (!role.contains("ADMIN")) {
            throw new InvalidRoleException("Forbidden Access");
        }

        return userMapper.toResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username : "+username)));
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String role) {

        if (!role.contains("ADMIN") && !role.contains("USER")) {
            throw new InvalidRoleException("Forbidden Access");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id : " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<UserResponse> getAllUsers(String role) {

        if (!role.contains("ADMIN")) {
            throw new InvalidRoleException("Forbidden Access");
        }

        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, String> fallback(Exception ex) {
        return Map.of("message", "Notification service is currently unavailable");
    }

}