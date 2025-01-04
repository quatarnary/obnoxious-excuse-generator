package com.ilyasbugra.excusegenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcuseDTO {
    private Long id;
    private String excuseMessage;
    private String category;
    private Date updatedAt;
}
