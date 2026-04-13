package com.cognizant.patientService.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()),
			HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleViolationException ex) {
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), LocalDateTime.now()),
			HttpStatus.CONFLICT
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex
			.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.findFirst()
			.orElse("Validation error");
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, LocalDateTime.now()),
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		return new ResponseEntity<>(
			new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Unexpected error occurred",
				LocalDateTime.now()
			),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	@ExceptionHandler(SlotAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleSlotAlreadyExists(SlotAlreadyExistsException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpStatus.CONFLICT.value());
		body.put("message", ex.getMessage());
		body.put("timestamp", LocalDateTime.now());
		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidRoleException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRole(InvalidRoleException ex) {
		return new ResponseEntity<>(
			new ErrorResponse(HttpStatus.FORBIDDEN.value(), "FORBIDDEN", LocalDateTime.now()),
			HttpStatus.FORBIDDEN
		);
	}
}
