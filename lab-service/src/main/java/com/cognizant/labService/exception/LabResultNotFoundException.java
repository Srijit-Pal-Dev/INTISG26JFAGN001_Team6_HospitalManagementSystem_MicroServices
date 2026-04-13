package com.cognizant.labService.exception;

public class LabResultNotFoundException extends RuntimeException {
    public LabResultNotFoundException(String message) {

        super("Lab result not found with id: " + message);
    }
}
