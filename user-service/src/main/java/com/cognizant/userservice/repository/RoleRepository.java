package com.cognizant.userservice.repository;

import com.cognizant.userservice.domain.Role;
import com.cognizant.userservice.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  RoleRepository extends JpaRepository<Role,Long> {
    
    Optional<Role> findByName(RoleName name);
}
