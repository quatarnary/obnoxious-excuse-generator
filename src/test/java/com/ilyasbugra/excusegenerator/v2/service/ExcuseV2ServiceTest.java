package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceTest {

    public static final String MESSAGE_1 = "excuse-message-1";
    public static final String MESSAGE_2 = "excuse-message-2";
    public static final String CATEGORY = "category";

    private static final User ADMIN_USER = User.builder()
            .id(UUID.randomUUID())
            .username("admin-lololololol")
            .password("password-go-brrrrrr")
            .userRole(UserRole.ADMIN)
            .build();
    private static final User MOD_USER = User.builder()
            .id(UUID.randomUUID())
            .username("mod-lololololol")
            .password("password-go-brrrrrr")
            .userRole(UserRole.MOD)
            .build();

    Pageable PAGEABLE_DEFAULT = PageRequest.of(0, 10);

    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    Random random;
    @Mock
    ExcuseV2Mapper excuseV2Mapper;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ExcuseV2Service excuseV2Service;

    @Test
    public void testGetAllExcuses() {
        // Arrange
        List<Excuse> excuses = createExcuses();
        Page<Excuse> page = new PageImpl<>(excuses, PAGEABLE_DEFAULT, excuses.size());
        List<ExcuseV2DTO> excuseV2DTOS = createExcuseV2DTOs(excuses);

        when(excuseRepository.findAll(PAGEABLE_DEFAULT))
                .thenReturn(page);

        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(excuseV2DTOS.get(0), excuseV2DTOS.get(1));

        // Act
        Page<ExcuseV2DTO> result = excuseV2Service.getAllExcuses(PAGEABLE_DEFAULT);

        // Assert
        assertNotNull(result);
        assertEquals(excuses.size(), result.getTotalElements());
        assertEquals(excuses.get(0).getExcuseMessage(), result.getContent().get(0).getExcuseMessage());
        assertEquals(excuses.get(1).getExcuseMessage(), result.getContent().get(1).getExcuseMessage());

        verify(excuseRepository).findAll(PAGEABLE_DEFAULT);
        verify(excuseV2Mapper, times(2)).toExcuseV2DTO(any(Excuse.class));
    }

    @Test
    public void testGetExcuseById() {
        // Arrange
        Excuse excuse = createExcuses().getFirst();
        ExcuseV2DTO excuseV2DTO = createExcuseV2DTOs(List.of(excuse)).getFirst();

        when(excuseRepository.findById(eq(excuse.getId())))
                .thenReturn(Optional.of(excuse));
        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(excuseV2DTO);

        // Act
        ExcuseV2DTO result = excuseV2Service.getExcuseById(excuse.getId());

        // Assert
        assertNotNull(result);
        assertEquals(excuse.getExcuseMessage(), result.getExcuseMessage());

        verify(excuseRepository).findById(excuse.getId());
        verify(excuseV2Mapper, times(1)).toExcuseV2DTO(any(Excuse.class));
    }

    // 🔹 Helper Methods 🔹
    private List<Excuse> createExcuses() {
        return List.of(
                Excuse.builder()
                        .excuseMessage(MESSAGE_1)
                        .category(CATEGORY)
                        .createdAt(new Date())
                        .createdBy(MOD_USER)
                        .approvedBy(ADMIN_USER)
                        .updatedAt(new Date())
                        .build(),
                Excuse.builder()
                        .excuseMessage(MESSAGE_2)
                        .category(CATEGORY)
                        .createdAt(new Date())
                        .createdBy(MOD_USER)
                        .approvedBy(ADMIN_USER)
                        .updatedAt(new Date())
                        .build()
        );
    }

    private List<ExcuseV2DTO> createExcuseV2DTOs(List<Excuse> excuses) {
        return excuses.stream()
                .map(excuse -> ExcuseV2DTO.builder()
                        .id(excuse.getId())
                        .excuseMessage(excuse.getExcuseMessage())
                        .category(excuse.getCategory())
                        .updatedAt(excuse.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
