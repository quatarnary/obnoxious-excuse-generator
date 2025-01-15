package com.ilyasbugra.excusegenerator.v1.mapper;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.v1.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.UpdateExcuseDTO;

public class ExcuseMapper {

    public static ExcuseDTO toExcuseDTO(Excuse excuse) {
        return ExcuseDTO.builder()
                .id(excuse.getId())
                .excuseMessage(excuse.getExcuseMessage())
                .category(excuse.getCategory())
                .updatedAt(excuse.getUpdatedAt())
                .build();
    }

    public static Excuse toExcuse(CreateExcuseDTO createExcuseDTO) {
        return Excuse.builder()
                .excuseMessage(createExcuseDTO.getExcuseMessage())
                .category(createExcuseDTO.getCategory())
                .build();
    }

    public static void updateExcuse(UpdateExcuseDTO updateExcuseDTO, Excuse excuse) {
        excuse.setExcuseMessage(updateExcuseDTO.getExcuseMessage());
        excuse.setCategory(updateExcuseDTO.getCategory());
    }
}
