package edu.com.bookingsystem.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityDeactivatedException.class)
    public ResponseEntity<Map<String, Object>> handleCourseExists(EntityDeactivatedException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFieldValueException.class)
    public ResponseEntity<Object> handleInvalidFieldValue(InvalidFieldValueException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("maxStudents", ex.getMessage())
        );
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEntityExists(EntityExistsException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityExists(EntityNotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(e -> {
            String fieldName = e.getPropertyPath().toString();
            errors.put(fieldName, e.getMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex)
    {
        Map<String, String> error = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> error.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid type");
        body.put("message", "Expected numeric value but got: " + ex.getValue());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();

        String paramName = ex.getParameterName();
        error.put(paramName, "Missing required request parameter: '" + paramName + "'"); // WHEN REQUESTPARAM IS MISSING OR DOES NOT MATCH AS THE PARAMETER

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUUID(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);

        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof IllegalArgumentException && cause.getMessage().contains("java.util.UUID")) {
            body.put("error", "Invalid UUID format");
            body.put("message", "One of the provided UUID fields is not in valid 36-character format.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        body.put("error", "Malformed JSON");
        body.put("message", "One of the provided fields could not be parsed.");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}