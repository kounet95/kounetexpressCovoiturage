package kounettechnologie.kounet.getwayservice;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        Map<String, List<String>> errors = new HashMap<>();

        constraintViolations.forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(cv.getMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}