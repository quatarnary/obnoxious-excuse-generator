package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
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
public class ExcuseV2ServiceUpdateTest {

    public static final UUID MOD_USER_ID = UUID.randomUUID();

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";

    public static final String UPDATE_MESSAGE = "updated-message";
    public static final String UPDATE_CATEGORY = "updated-category";
    public static final UpdateExcuseV2DTO UPDATE_EXCUSE_V2_DTO = UpdateExcuseV2DTO.builder()
            .excuseMessage(UPDATE_MESSAGE)
            .category(UPDATE_CATEGORY)
            .build();
    private static final User MOD_USER = User.builder()
            .id(MOD_USER_ID)
            .username("mod-lololololol")
            .password("password-go-brrrrrr")
            .userRole(UserRole.MOD)
            .build();
    public static final Excuse EXCUSE = Excuse.builder()
            .id(EXCUSE_ID)
            .excuseMessage(MESSAGE)
            .category(CATEGORY)
            .createdBy(MOD_USER)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();
    public static final ExcuseV2DTO EXCUSE_V2_DTO = ExcuseV2DTO.builder()
            .id(EXCUSE.getId())
            .excuseMessage(EXCUSE.getExcuseMessage())
            .category(EXCUSE.getCategory())
            .updatedAt(EXCUSE.getUpdatedAt())
            .build();
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;
    @Mock
    UserRepository userRepository;
    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    ExcuseV2Mapper excuseV2Mapper;

    @InjectMocks
    ExcuseV2Service excuseV2Service;

    @Test
    public void testUpdateExcuse_Mod_CreatedBySelf() {
        /// Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(MOD_USER.getUsername());
        when(userRepository.findByUsername(MOD_USER.getUsername()))
                .thenReturn(Optional.of(MOD_USER));
        when(excuseRepository.findById(EXCUSE_ID))
                .thenReturn(Optional.of(EXCUSE));
        doAnswer(invocation -> {
            UpdateExcuseV2DTO mapperUpdateExcuseV2DTO = invocation.getArgument(0);
            Excuse mapperExcuse = invocation.getArgument(1);

            mapperExcuse.setExcuseMessage(mapperUpdateExcuseV2DTO.getExcuseMessage());
            mapperExcuse.setCategory(mapperUpdateExcuseV2DTO.getCategory());

            EXCUSE.setExcuseMessage(mapperUpdateExcuseV2DTO.getExcuseMessage());
            EXCUSE.setCategory(mapperUpdateExcuseV2DTO.getCategory());

            return null;
        }).when(excuseV2Mapper).updateExcuseV2(UPDATE_EXCUSE_V2_DTO, EXCUSE);
        when(excuseRepository.save(EXCUSE))
                .thenReturn(EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.updateExcuse(EXCUSE_ID, UPDATE_EXCUSE_V2_DTO);

        ArgumentCaptor<Excuse> excuseCaptor = ArgumentCaptor.forClass(Excuse.class);

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(MOD_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseV2Mapper).updateExcuseV2(UPDATE_EXCUSE_V2_DTO, EXCUSE);
        verify(excuseRepository).save(excuseCaptor.capture());
        verify(excuseV2Mapper).toExcuseV2DTO(EXCUSE);

        Excuse updatedExcuse = excuseCaptor.getValue();

        /// Assert
        assertNotNull(result);
        assertEquals(EXCUSE_ID, updatedExcuse.getId());
        assertEquals(UPDATE_MESSAGE, updatedExcuse.getExcuseMessage());
        assertEquals(UPDATE_CATEGORY, updatedExcuse.getCategory());
        assertEquals(MOD_USER_ID, updatedExcuse.getUpdatedBy().getId());
        assertEquals(EXCUSE.getCreatedBy().getId(), updatedExcuse.getCreatedBy().getId());
    }
}
