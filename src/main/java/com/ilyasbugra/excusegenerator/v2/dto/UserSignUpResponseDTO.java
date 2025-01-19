package com.ilyasbugra.excusegenerator.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponseDTO {

    private String username;
    private String message;
}
