package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.util.ExceptionResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExcuseExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(ExcuseExceptionHandler.class);

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex) {
        logger.warn("Invalid input: {}", ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        logger.warn("Validation errors: {}", fieldErrors);

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                        HttpStatus.BAD_REQUEST,
                        ErrorMessages.VALIDATION_ERROR)
                .fields(fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExcuseNotFoundException.class)
    public ResponseEntity<Object> handleExcuseNotFound(ExcuseNotFoundException ex) {
        logger.warn("Excuse not found: {}", ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExcuseCategoryNotFoundException.class)
    public ResponseEntity<Object> handleExcuseCategoryNotFound(ExcuseCategoryNotFoundException ex) {
        logger.warn("Excuse category not found: {}", ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
