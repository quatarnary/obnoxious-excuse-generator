package com.ilyasbugra.excusegenerator.v2.mapper;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExcuseV2Mapper {

    ExcuseV2DTO toExcuseV2DTO(Excuse excuse);

    @Mapping(target = "id", ignore = true)
    Excuse toExcuse(CreateExcuseV2DTO createExcuseV2DTO);

    void updateExcuseV2(UpdateExcuseV2DTO updateExcuseV2DTO, @MappingTarget Excuse excuse);
}
