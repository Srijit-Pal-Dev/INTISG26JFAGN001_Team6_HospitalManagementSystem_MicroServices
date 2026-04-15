package com.cognizant.userservice.service;

import com.cognizant.userservice.client.NotificationClient;
import com.cognizant.userservice.domain.NotificationType;
import com.cognizant.userservice.domain.Role;
import com.cognizant.userservice.domain.RoleName;
import com.cognizant.userservice.domain.User;
import com.cognizant.userservice.dto.CreateUserRequest;
import com.cognizant.userservice.dto.UserResponse;
import com.cognizant.userservice.exception.InvalidRoleException;
import com.cognizant.userservice.mapper.UserMapper;
import com.cognizant.userservice.repository.RoleRepository;
import com.cognizant.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .name(RoleName.USER)
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setPassword("encoded-password");
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setFullName("Test User");
    }

    // -------------------- CREATE USER --------------------

    @Test
    void testCreateUser_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setRoles(Set.of(RoleName.USER));

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(roleRepository.findByName(RoleName.USER))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("password"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response =
                userService.createUser(request, "ROLE_ADMIN");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        verify(notificationClient, times(1))
                .send(any());
    }

    @Test
    void testCreateUser_invalidRole() {
        CreateUserRequest request = new CreateUserRequest();

        assertThrows(
                InvalidRoleException.class,
                () -> userService.createUser(request, "ROLE_GUEST")
        );
    }

    // -------------------- GET USER BY ID --------------------

    @Test
    void testGetUserById_success() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response =
                userService.getUserById(1L, "ROLE_USER");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    // -------------------- GET USER BY USERNAME --------------------

    @Test
    void testGetUserByUsername_success() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse response =
                userService.getUserByUsername("testuser", "ROLE_ADMIN");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    // -------------------- DELETE USER --------------------

    @Test
    void testDeleteUser_success() {
        when(userRepository.existsById(1L))
                .thenReturn(true);

        userService.deleteUser(1L, "ROLE_ADMIN");

        verify(userRepository, times(1))
                .deleteById(1L);
    }

    // -------------------- GET ALL USERS --------------------

    @Test
    void testGetAllUsers_success() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        List<UserResponse> responses =
                userService.getAllUsers("ROLE_USER");

        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).getUsername());
    }
}