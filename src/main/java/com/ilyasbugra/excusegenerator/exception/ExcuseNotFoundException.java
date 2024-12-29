package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.ErrorMessages;

public class ExcuseNotFoundException extends RuntimeException {

    public ExcuseNotFoundException(Long id) {
        super(String.format(ErrorMessages.EXCUSE_NOT_FOUND, id));
    }
}
