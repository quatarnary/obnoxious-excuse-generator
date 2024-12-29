package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.ErrorMessages;

public class ExcuseCategoryNotFoundException extends RuntimeException {

    public ExcuseCategoryNotFoundException(String category) {
        super(String.format(ErrorMessages.CATEGORY_NOT_FOUND, category));
    }
}
