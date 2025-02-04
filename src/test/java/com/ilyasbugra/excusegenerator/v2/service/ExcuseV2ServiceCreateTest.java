package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceCreateTest {

    public static final String MESSAGE = "excuse-message";
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


    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    ExcuseV2Mapper excuseV2Mapper;
    @Mock
    UserRepository userRepository;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;

    @InjectMocks
    ExcuseV2Service excuseV2Service;

    // ⚠️ WARNING: THIS TEST SUITE IS FOR THE CURRENT "PAIN" VERSION ⚠️
    // Once we refactor ExcuseV2Service into a cleaner, more DDD-based structure,
    // we will need to rewrite these tests to match the new design.
    // But for now, this ensures the current implementation is covered before refactoring.

    @Test
    public void testCreateExcuse_Mod() {
        /// Arrange
        CreateExcuseV2DTO createExcuseV2DTO = CreateExcuseV2DTO.builder()
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .build();
        Excuse excuse = Excuse.builder()
                .id(0L)
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        ExcuseV2DTO excuseV2DTO = ExcuseV2DTO.builder()
                .id(excuse.getId())
                .excuseMessage(excuse.getExcuseMessage())
                .category(excuse.getCategory())
                .updatedAt(excuse.getUpdatedAt())
                .build();

        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(MOD_USER.getUsername());
        when(userRepository.findByUsername(MOD_USER.getUsername()))
                .thenReturn(Optional.of(MOD_USER));
        when(excuseV2Mapper.toExcuse(createExcuseV2DTO))
                .thenReturn(excuse);
        when(excuseRepository.save(excuse))
                .thenReturn(excuse);
        when(excuseV2Mapper.toExcuseV2DTO(excuse))
                .thenReturn(excuseV2DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.createExcuse(createExcuseV2DTO);

        /// Assert
        ArgumentCaptor<Excuse> excuseCaptor = ArgumentCaptor.forClass(Excuse.class);

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository, times(1)).findByUsername(MOD_USER.getUsername());
        verify(excuseV2Mapper, times(1)).toExcuse(createExcuseV2DTO);
        verify(excuseRepository, times(1)).save(excuseCaptor.capture());
        verify(excuseV2Mapper, times(1)).toExcuseV2DTO(excuse);

        Excuse savedExcuse = excuseCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(savedExcuse.getCreatedBy());
        assertEquals(MESSAGE, result.getExcuseMessage());
        assertEquals(CATEGORY, result.getCategory());
        assertEquals(MOD_USER.getUsername(), savedExcuse.getCreatedBy().getUsername());
    }
}
