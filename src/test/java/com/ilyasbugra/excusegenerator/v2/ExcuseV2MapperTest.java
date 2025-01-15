package com.ilyasbugra.excusegenerator.v2;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExcuseV2MapperTest {

    @Autowired
    private ExcuseV2Mapper excuseV2Mapper;

    @Test
    void testToExcuseV2DTO() {
        Excuse excuse = Excuse.builder()
                .id(1L)
                .excuseMessage("The coffee spilled itself!")
                .category("Work")
                .build();

        ExcuseV2DTO excuseDTO = excuseV2Mapper.toExcuseV2DTO(excuse);

        assertEquals("The coffee spilled itself!", excuseDTO.getExcuseMessage());
        assertEquals("Work", excuseDTO.getCategory());
    }
}
