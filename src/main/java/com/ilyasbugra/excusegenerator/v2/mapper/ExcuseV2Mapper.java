package com.ilyasbugra.excusegenerator.v2.mapper;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;

public class ExcuseV2Mapper {

    public static ExcuseV2DTO toExcuseV2DTO(Excuse excuse) {
        return ExcuseV2DTO.builder()
                .id(excuse.getId())
                .excuseMessage(excuse.getExcuseMessage())
                .category(excuse.getCategory())
                .updatedAt(excuse.getUpdatedAt())
                .build();
    }

    public static Excuse toExcuseV2(CreateExcuseV2DTO createExcuseV2DTO) {
        return Excuse.builder()
                .excuseMessage(createExcuseV2DTO.getExcuseMessage())
                .category(createExcuseV2DTO.getCategory())
                .build();
    }

    public static void updateExcuseV2(UpdateExcuseV2DTO updateExcuseV2DTO, Excuse excuse) {
        excuse.setExcuseMessage(updateExcuseV2DTO.getExcuseMessage());
        excuse.setCategory(updateExcuseV2DTO.getCategory());
    }
}
