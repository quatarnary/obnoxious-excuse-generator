package com.ilyasbugra.excusegenerator.util;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class ExceptionResponseBuilder {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private Map<String, String> fields;
    private Map<String, Object> extras;

    public static ExceptionResponseBuilder create(HttpStatus status, String message) {
        return ExceptionResponseBuilder.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
    }

    // Add a method for fields (Fixing IntelliJ Error)
    public ExceptionResponseBuilder fields(Map<String, String> fieldErrors) {
        this.fields = fieldErrors;
        return this;
    }

    // Optional method for adding extras
    public ExceptionResponseBuilder addExtras(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<>();
        }
        extras.put(key, value);
        return this;
    }
}
