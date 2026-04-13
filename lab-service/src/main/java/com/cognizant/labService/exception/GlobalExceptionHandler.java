package com.cognizant.labService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handle LabTest not found
    @ExceptionHandler(LabTestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(LabTestNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Handle LabResult not found
    @ExceptionHandler(LabResultNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLabResultNotFound(LabResultNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        return Map.of(
                "error",
                ex.getBindingResult().getFieldError().getDefaultMessage()
        );
    }

    // Handle generic runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntime(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleInvalidRole(InvalidRoleException ex) {
        return Map.of("error", ex.getMessage());
    }

}