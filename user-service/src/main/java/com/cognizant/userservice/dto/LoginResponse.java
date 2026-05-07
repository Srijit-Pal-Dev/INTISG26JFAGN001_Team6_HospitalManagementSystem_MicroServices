package com.cognizant.userservice.dto;

import com.cognizant.userservice.domain.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long id;

    private String username;

    private String roles;

    private String fullName;

}
