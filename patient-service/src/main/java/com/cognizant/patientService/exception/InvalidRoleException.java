package com.cognizant.patientService.exception;

public class InvalidRoleException extends RuntimeException {

	public InvalidRoleException(String role) {
		super(role);
	}
}
