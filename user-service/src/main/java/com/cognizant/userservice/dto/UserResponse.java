package com.cognizant.userservice.dto;

import com.cognizant.userservice.domain.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String username;

    private String fullName;

    private boolean enabled;

    private Set<RoleName> roles;

    private Instant createdAt;

    private Instant updatedAt;

}
