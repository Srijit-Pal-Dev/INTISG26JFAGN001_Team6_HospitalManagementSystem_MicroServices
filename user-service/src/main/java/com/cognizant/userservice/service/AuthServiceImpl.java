package com.cognizant.userservice.service;

import com.cognizant.userservice.client.NotificationClient;
import com.cognizant.userservice.config.JwtUtil;
import com.cognizant.userservice.domain.*;
import com.cognizant.userservice.dto.*;
import com.cognizant.userservice.repository.RefreshTokenRepository;
import com.cognizant.userservice.repository.RoleRepository;
import com.cognizant.userservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationClient notificationClient;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        System.out.println(refreshToken);
        refreshTokenRepository.deleteByToken(refreshToken.trim());
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> RefreshToken.builder().user(user).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        try {
            return refreshTokenRepository.save(refreshToken);
        } catch (DataIntegrityViolationException ex) {
            RefreshToken existingToken = refreshTokenRepository.findByUser(user)
                    .orElseThrow(() -> ex);
            existingToken.setToken(UUID.randomUUID().toString());
            existingToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
            return refreshTokenRepository.save(existingToken);
        }
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh Token expired");
        }

        return token;
    }

    @Override
    @CircuitBreaker(name="myCircuitBreaker", fallbackMethod = "fallback")
    public Map<String, String> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow();

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        RefreshToken refreshToken = createRefreshToken(authentication.getName());

        String userRole = roles.stream()
                .findFirst()
                .map(role -> role.replace("ROLE_", ""))
                .orElse("USER");

        SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setUserId(user.getId());
        sendNotificationRequest.setTitle("User Login");
        sendNotificationRequest.setMessage(userRole+" "+user.getFullName()+" has logged in");
        sendNotificationRequest.setType(NotificationType.GENERAL);

        notificationClient.send(sendNotificationRequest);


        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );

    }

    @Override
    public Map<String, String> refreshAccessToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = verifyExpiration(
                refreshTokenRepository.findByToken(request.getRefreshToken())
                        .orElseThrow(() -> new RuntimeException("Invalid Refresh Token")));

        User user = refreshToken.getUser();

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        String newAccessToken = jwtUtil.generateToken(user.getUsername(),roles);

        return Map.of("New accessToken",newAccessToken);
    }

    @Override
    @CircuitBreaker(name="myCircuitBreaker", fallbackMethod = "fallback")
    public Map<String, String> register(RegisterRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            return Map.of("message", "Username is already taken");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("User Role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of(userRole))
                .enabled(true)
                .build();


        userRepository.save(user);

        SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setUserId(user.getId());
        sendNotificationRequest.setTitle("User Registration");
        sendNotificationRequest.setMessage(userRole+" "+user.getFullName()+" is registered");
        sendNotificationRequest.setType(NotificationType.GENERAL);

        notificationClient.send(sendNotificationRequest);

        return Map.of("message","User registered successfully");
    }

    public Map<String, String> fallback(Exception ex) {
        return Map.of("message", "Notification service is currently unavailable");
    }
}
