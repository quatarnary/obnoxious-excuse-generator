package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.UserNotAuthorized;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.UserErrorMessages;
import com.ilyasbugra.excusegenerator.v2.actions.mod.ModUser;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import com.ilyasbugra.excusegenerator.v2.util.ExcuseHelper;
import com.ilyasbugra.excusegenerator.v2.util.UserHelper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceUpdateTest {

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";
    public static final String UPDATE_MESSAGE = "updated-message";
    public static final String UPDATE_CATEGORY = "updated-category";

    public UpdateExcuseV2DTO UPDATE_EXCUSE_V2_DTO;
    public Excuse EXCUSE;
    public Excuse MODIFIED_EXCUSE;
    public ExcuseV2DTO EXCUSE_V2_DTO;

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
    @Mock
    UserHelper userHelper;
    @Mock
    ExcuseHelper excuseHelper;
    @Mock
    ModUser modUser;
    @InjectMocks
    ExcuseV2Service excuseV2Service;

    private User MOD_USER;
    private User SECOND_MOD_USER;
    private User ADMIN_USER;

    @BeforeEach
    void setUp() {
        MOD_USER = User.builder()
                .id(UUID.randomUUID())  // Unique ID per test
                .username("mod-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.MOD)
                .build();

        SECOND_MOD_USER = User.builder()
                .id(UUID.randomUUID())  // Unique ID per test
                .username("imma-be-second-mod-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.MOD)
                .build();

        ADMIN_USER = User.builder()
                .id(UUID.randomUUID())  // Unique ID per test
                .username("admin-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.ADMIN)
                .build();

        EXCUSE = Excuse.builder()
                .id(EXCUSE_ID)
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .createdBy(MOD_USER)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        MODIFIED_EXCUSE = Excuse.builder()
                .id(EXCUSE_ID)
                .excuseMessage(UPDATE_MESSAGE)
                .category(UPDATE_CATEGORY)
                .createdBy(MOD_USER)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        EXCUSE_V2_DTO = ExcuseV2DTO.builder()
                .id(MODIFIED_EXCUSE.getId())
                .excuseMessage(MODIFIED_EXCUSE.getExcuseMessage())
                .category(MODIFIED_EXCUSE.getCategory())
                .updatedAt(MODIFIED_EXCUSE.getUpdatedAt())
                .build();

        UPDATE_EXCUSE_V2_DTO = UpdateExcuseV2DTO.builder()
                .excuseMessage(UPDATE_MESSAGE)
                .category(UPDATE_CATEGORY)
                .build();
    }

    @Test
    public void testUpdateExcuse_Mod_CreatedBySelf() {
        /// Arrange
        when(userHelper.getAuthenticatedUser())
                .thenReturn(MOD_USER);
        when(excuseHelper.getExcuseById(EXCUSE_ID))
                .thenReturn(EXCUSE);
        doAnswer(invocation -> true)
                .when(modUser).updateExcuse(EXCUSE, MOD_USER);
        when(excuseV2Mapper.updateExcuseV2(UPDATE_EXCUSE_V2_DTO, EXCUSE))
                .thenReturn(MODIFIED_EXCUSE);
        when(excuseRepository.save(MODIFIED_EXCUSE))
                .thenReturn(MODIFIED_EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(MODIFIED_EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.updateExcuse(EXCUSE_ID, UPDATE_EXCUSE_V2_DTO);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseHelper).getExcuseById(EXCUSE_ID);
        verify(modUser).updateExcuse(EXCUSE, MOD_USER);
        verify(excuseV2Mapper).updateExcuseV2(UPDATE_EXCUSE_V2_DTO, EXCUSE);
        verify(excuseRepository).save(MODIFIED_EXCUSE);
        verify(excuseV2Mapper).toExcuseV2DTO(MODIFIED_EXCUSE);

        /// Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(MODIFIED_EXCUSE.getId(), result.getId());
        assertEquals(MODIFIED_EXCUSE.getExcuseMessage(), result.getExcuseMessage());
        assertEquals(MODIFIED_EXCUSE.getCategory(), result.getCategory());
    }

    @Test
    public void testUpdateExcuse_Mod_Not_CreatedBySelf() {
        /// Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(SECOND_MOD_USER.getUsername());
        when(userRepository.findByUsername(SECOND_MOD_USER.getUsername()))
                .thenReturn(Optional.of(SECOND_MOD_USER));
        when(excuseRepository.findById(EXCUSE_ID))
                .thenReturn(Optional.of(EXCUSE));

        /// Act
        UserNotAuthorized thrown = assertThrows(
                UserNotAuthorized.class,
                () -> excuseV2Service.updateExcuse(EXCUSE_ID, UPDATE_EXCUSE_V2_DTO)
        );

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(SECOND_MOD_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseV2Mapper, times(0)).updateExcuseV2(UPDATE_EXCUSE_V2_DTO, EXCUSE);

        /// Assert
        assertNotNull(thrown);
        assertEquals(String.format(UserErrorMessages.USER_NOT_AUTHORIZED, SECOND_MOD_USER.getUsername()), thrown.getMessage());
    }

    @Test
    public void testUpdateExcuse_Admin_Not_CreatedBySelf() {
        /// Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(ADMIN_USER.getUsername());
        when(userRepository.findByUsername(ADMIN_USER.getUsername()))
                .thenReturn(Optional.of(ADMIN_USER));
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
        verify(userRepository).findByUsername(ADMIN_USER.getUsername());
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
        assertEquals(ADMIN_USER.getId(), updatedExcuse.getUpdatedBy().getId());
        assertEquals(EXCUSE.getCreatedBy().getId(), updatedExcuse.getCreatedBy().getId());
        assertNotEquals(updatedExcuse.getCreatedBy().getId(), updatedExcuse.getUpdatedBy().getId());
    }
}
