package com.cognizant.billingService.exception;

public class InvalidRoleException extends RuntimeException {

	public InvalidRoleException(String role) {
		super(role);
	}
}
