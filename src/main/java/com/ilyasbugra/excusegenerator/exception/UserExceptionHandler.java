package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.ExceptionResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);

    @ExceptionHandler(UserNotAuthorized.class)
    public ResponseEntity<ExceptionResponseBuilder> handleUserNotAuthorized(UserNotAuthorized ex) {
        logger.warn(ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Object> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException ex) {
        logger.warn(ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn(ex.getMessage());

        ExceptionResponseBuilder response = ExceptionResponseBuilder.create(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
