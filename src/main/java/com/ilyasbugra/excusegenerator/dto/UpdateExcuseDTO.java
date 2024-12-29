package com.ilyasbugra.excusegenerator.dto;

import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExcuseDTO {

    @NotBlank(message = ErrorMessages.EMPTY_EXCUSE_MESSAGE)
    @Size(max = 255, message = ErrorMessages.LARGE_EXCUSE_MESSAGE)
    private String excuseMessage;

    @NotBlank(message = ErrorMessages.EMPTY_CATEGORY)
    @Size(max = 50, message = ErrorMessages.LARGE_CATEGORY)
    private String category;
}
