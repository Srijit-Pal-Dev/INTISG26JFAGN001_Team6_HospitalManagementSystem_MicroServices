package com.cognizant.labService.exception;

public class LabTestNotFoundException extends RuntimeException {
    public LabTestNotFoundException(Long id){
        super("Lab test not found with id: " + id);
    }
}
