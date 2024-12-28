package com.ilyasbugra.excusegenerator.exception;

public class ExcuseCategoryNotFoundException extends RuntimeException {

    public ExcuseCategoryNotFoundException(String category) {
        super("Excuse Category " + category + " not found. Check for typos and try again.");
    }
}
