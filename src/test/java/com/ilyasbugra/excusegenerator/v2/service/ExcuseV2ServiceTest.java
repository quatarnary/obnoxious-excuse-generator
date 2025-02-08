package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.util.ExcuseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceTest {

    public static final Long EXCUSE_ID_1 = 1L;
    public static final Long EXCUSE_ID_2 = 2L;

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
    ExcuseV2Mapper excuseV2Mapper;
    @Mock
    ExcuseHelper excuseHelper;
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
    public void testGetAllExcuses_EmptyDatabase() {
        // Arrange
        Page<Excuse> page = new PageImpl<>(List.of(), PAGEABLE_DEFAULT, 0);

        when(excuseRepository.findAll(PAGEABLE_DEFAULT))
                .thenReturn(page);

        // Act
        Page<ExcuseV2DTO> result = excuseV2Service.getAllExcuses(PAGEABLE_DEFAULT);

        // Assert
        assertNotNull(result);
        assertEquals(page.getTotalElements(), result.getTotalElements());

        verify(excuseRepository).findAll(PAGEABLE_DEFAULT);
        verify(excuseV2Mapper, times(0)).toExcuseV2DTO(any(Excuse.class));
    }

    @Test
    public void testGetExcuseById() {
        // Arrange
        Excuse excuse = createExcuses().getFirst();
        ExcuseV2DTO excuseV2DTO = createExcuseV2DTOs(List.of(excuse)).getFirst();

        when(excuseHelper.getExcuseById(EXCUSE_ID_1))
                .thenReturn(excuse);
        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(excuseV2DTO);

        // Act
        ExcuseV2DTO result = excuseV2Service.getExcuseById(excuse.getId());

        // Assert
        assertNotNull(result);
        assertEquals(excuse.getExcuseMessage(), result.getExcuseMessage());

        verify(excuseHelper).getExcuseById(EXCUSE_ID_1);
        verify(excuseV2Mapper, times(1)).toExcuseV2DTO(any(Excuse.class));
    }

    @Test
    public void testGetRandomExcuse() {
        // Arrange
        Excuse excuse = createExcuses().getFirst();
        ExcuseV2DTO excuseV2DTO = createExcuseV2DTOs(List.of(excuse)).getFirst();

        when(excuseHelper.getRandomExcuse())
                .thenReturn(excuse);
        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(excuseV2DTO);

        // Act
        ExcuseV2DTO result = excuseV2Service.getRandomExcuse();

        // Assert
        assertNotNull(result);
        assertEquals(excuse.getExcuseMessage(), result.getExcuseMessage());

        verify(excuseHelper).getRandomExcuse();
        verify(excuseV2Mapper).toExcuseV2DTO(any(Excuse.class));
    }

    @Test
    public void testGetExcusesByCategory() {
        // Arrange
        List<Excuse> excuses = createExcuses();
        Page<Excuse> page = new PageImpl<>(excuses, PAGEABLE_DEFAULT, excuses.size());
        List<ExcuseV2DTO> excuseV2DTOS = createExcuseV2DTOs(excuses);

        when(excuseHelper.getExcusesByCategory(CATEGORY, PAGEABLE_DEFAULT))
                .thenReturn(page);
        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(excuseV2DTOS.get(0), excuseV2DTOS.get(1));

        // Act
        Page<ExcuseV2DTO> result = excuseV2Service.getExcusesByCategory(CATEGORY, PAGEABLE_DEFAULT);

        verify(excuseHelper).getExcusesByCategory(CATEGORY, PAGEABLE_DEFAULT);
        verify(excuseV2Mapper, times(2)).toExcuseV2DTO(any(Excuse.class));

        // Assert
        assertNotNull(result);
        assertEquals(excuses.size(), result.getTotalElements());
        assertEquals(excuseV2DTOS.get(0).getExcuseMessage(), result.getContent().get(0).getExcuseMessage());
        assertEquals(excuseV2DTOS.get(1).getExcuseMessage(), result.getContent().get(1).getExcuseMessage());
    }

    @Test
    public void testGetExcusesByCategory_NonExistentCategory() {
        // Arrange
        Page<Excuse> page = Page.empty();

        when(excuseRepository.findByCategoryStartingWithIgnoreCase(CATEGORY, PAGEABLE_DEFAULT))
                .thenReturn(page);

        // Act
        ExcuseCategoryNotFoundException thrown = assertThrows(
                ExcuseCategoryNotFoundException.class,
                () -> excuseV2Service.getExcusesByCategory(CATEGORY, PAGEABLE_DEFAULT)
        );

        // Assert
        assertNotNull(thrown);
        assertEquals(String.format(ErrorMessages.CATEGORY_NOT_FOUND, CATEGORY), thrown.getMessage());

        verify(excuseRepository).findByCategoryStartingWithIgnoreCase(CATEGORY, PAGEABLE_DEFAULT);
        verify(excuseV2Mapper, times(0)).toExcuseV2DTO(any(Excuse.class));
    }

    // 🔹 Helper Methods 🔹
    private List<Excuse> createExcuses() {
        return List.of(
                Excuse.builder()
                        .id(EXCUSE_ID_1)
                        .excuseMessage(MESSAGE_1)
                        .category(CATEGORY)
                        .createdAt(new Date())
                        .createdBy(MOD_USER)
                        .approvedBy(ADMIN_USER)
                        .updatedAt(new Date())
                        .build(),
                Excuse.builder()
                        .id(EXCUSE_ID_2)
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
