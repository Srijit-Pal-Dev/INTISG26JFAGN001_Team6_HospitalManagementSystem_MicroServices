package com.cognizant.userservice.service;

import com.cognizant.userservice.client.NotificationClient;
import com.cognizant.userservice.config.JwtUtil;
import com.cognizant.userservice.domain.*;
import com.cognizant.userservice.dto.*;
import com.cognizant.userservice.repository.RefreshTokenRepository;
import com.cognizant.userservice.repository.RoleRepository;
import com.cognizant.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = Role.builder()
                .id(1L)
                .name(RoleName.USER)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded-password")
                .fullName("Test User")
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void testLogin_success() {
        // ✅ Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");


        doReturn(List.of(new SimpleGrantedAuthority("USER")))
                .when(authentication)
                .getAuthorities();


        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(any(), any(), any()))
                .thenReturn("access-token");

        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiryDate(Instant.now().plusSeconds(600))
                .user(user)
                .build();

        when(refreshTokenRepository.findByUser(user))
                .thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any()))
                .thenReturn(refreshToken);

        // ✅ Act
        Map<String, String> response = authService.login(request);

        // ✅ Assert
        assertNotNull(response);
        assertEquals("access-token", response.get("accessToken"));
        assertEquals("refresh-token", response.get("refreshToken"));

        verify(notificationClient, times(1)).send(any());
    }

    @Test
    void testRegister_success() {
        // ✅ Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setFullName("New User");

        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        when(roleRepository.findByName(RoleName.USER))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("password"))
                .thenReturn("encoded-password");

        when(userRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ✅ Act
        Map<String, String> response = authService.register(request);

        // ✅ Assert
        assertEquals("User registered successfully", response.get("message"));
        verify(notificationClient, times(1)).send(any());
    }

    @Test
    void testRegister_usernameAlreadyExists() {
        // ✅ Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        // ✅ Act
        Map<String, String> response = authService.register(request);

        // ✅ Assert
        assertEquals("Username is already taken", response.get("message"));
        verify(notificationClient, never()).send(any());
    }

    @Test
    void testLogout_success() {
        // ✅ Act
        authService.logout("refresh-token");

        // ✅ Assert
        verify(refreshTokenRepository, times(1))
                .deleteByToken("refresh-token");
    }

    @Test
    void testRefreshAccessToken_success() {
        // ✅ Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiryDate(Instant.now().plusSeconds(600))
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        when(jwtUtil.generateToken(anyLong(), anyString(), anyList()))
                .thenReturn("new-access-token");

        // ✅ Act
        Map<String, String> response = authService.refreshAccessToken(request);

        // ✅ Assert
        assertEquals("new-access-token", response.get("New accessToken"));
    }
}