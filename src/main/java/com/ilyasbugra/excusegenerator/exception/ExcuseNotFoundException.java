package com.ilyasbugra.excusegenerator.exception;

public class ExcuseNotFoundException extends RuntimeException {

    public ExcuseNotFoundException(Long id) {
        super("Excuse with ID " + id + " not found.");
    }
}
