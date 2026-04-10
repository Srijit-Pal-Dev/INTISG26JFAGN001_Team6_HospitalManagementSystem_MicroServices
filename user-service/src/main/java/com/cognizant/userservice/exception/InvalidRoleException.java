package com.cognizant.userservice.exception;

public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String role) {
        super(role);
    }
}
