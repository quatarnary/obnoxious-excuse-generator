package com.ilyasbugra.excusegenerator.dto;

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

    @NotBlank(message = "My soul may be empty but Excuse Message cannot be empty")
    @Size(max = 255, message = "Excuse message cannot exceed 255 characters")
    private String excuseMessage;

    @NotBlank(message = "I may not have a direction but Category cannot be empty")
    @Size(max = 50, message = "Excuse category cannot exceed 50 characters")
    private String category;
}
