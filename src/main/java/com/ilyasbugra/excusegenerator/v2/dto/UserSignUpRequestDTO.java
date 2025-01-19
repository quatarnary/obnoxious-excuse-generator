package com.ilyasbugra.excusegenerator.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
