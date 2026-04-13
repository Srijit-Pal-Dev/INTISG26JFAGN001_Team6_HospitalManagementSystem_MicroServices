package com.cognizant.labService.exception;

public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String role) {
        super(role);
    }
}
