package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.UserNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.UserErrorMessages;
import com.ilyasbugra.excusegenerator.v2.actions.mod.ModUser;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
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
public class ExcuseV2ServiceCreateTest {

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";
    public Excuse EXCUSE;
    public ExcuseV2DTO EXCUSE_V2_DTO;
    public CreateExcuseV2DTO CREATE_EXCUSE_V2_DTO;
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
    @Mock
    UserHelper userHelper;
    @Mock
    ModUser modUser;
    @InjectMocks
    ExcuseV2Service excuseV2Service;
    private User ADMIN_USER;
    private User MOD_USER;

    @BeforeEach
    void setUp() {
        EXCUSE = Excuse.builder()
                .id(EXCUSE_ID)
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        EXCUSE_V2_DTO = ExcuseV2DTO.builder()
                .id(EXCUSE.getId())
                .excuseMessage(EXCUSE.getExcuseMessage())
                .category(EXCUSE.getCategory())
                .updatedAt(EXCUSE.getUpdatedAt())
                .build();

        CREATE_EXCUSE_V2_DTO = CreateExcuseV2DTO.builder()
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .build();

        ADMIN_USER = User.builder()
                .id(UUID.randomUUID()) // Ensures fresh UUID per test
                .username("admin-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.ADMIN)
                .build();

        MOD_USER = User.builder()
                .id(UUID.randomUUID()) // Ensures fresh UUID per test
                .username("mod-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.MOD)
                .build();
    }

    // ⚠️ WARNING: THIS TEST SUITE IS FOR THE CURRENT "PAIN" VERSION ⚠️
    // Once we refactor ExcuseV2Service into a cleaner, more DDD-based structure,
    // we will need to rewrite these tests to match the new design.
    // But for now, this ensures the current implementation is covered before refactoring.

    @Test
    public void testCreateExcuse_Mod() {
        /// Arrange
        when(userHelper.getAuthenticatedUser())
                .thenReturn(MOD_USER);
        when(excuseV2Mapper.toExcuse(CREATE_EXCUSE_V2_DTO))
                .thenReturn(EXCUSE);
        doNothing().when(modUser).createExcuse(EXCUSE, MOD_USER);
        when(excuseRepository.save(EXCUSE))
                .thenReturn(EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseV2Mapper).toExcuse(CREATE_EXCUSE_V2_DTO);
        verify(modUser).createExcuse(EXCUSE, MOD_USER);
        verify(excuseRepository).save(EXCUSE);
        verify(excuseV2Mapper).toExcuseV2DTO(EXCUSE);

        /// Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getUpdatedAt());
        assertEquals(MESSAGE, result.getExcuseMessage());
        assertEquals(CATEGORY, result.getCategory());
    }

    @Test
    public void testCreateExcuse_Admin() {
        ///  Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(ADMIN_USER.getUsername());
        when(userRepository.findByUsername(ADMIN_USER.getUsername()))
                .thenReturn(Optional.of(ADMIN_USER));
        when(excuseV2Mapper.toExcuse(CREATE_EXCUSE_V2_DTO))
                .thenReturn(EXCUSE);
        when(excuseRepository.save(EXCUSE))
                .thenReturn(EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO);

        ArgumentCaptor<Excuse> excuseCaptor = ArgumentCaptor.forClass(Excuse.class);

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository, times(1)).findByUsername(ADMIN_USER.getUsername());
        verify(excuseV2Mapper, times(1)).toExcuse(CREATE_EXCUSE_V2_DTO);
        verify(excuseRepository, times(1)).save(excuseCaptor.capture());
        verify(excuseV2Mapper, times(1)).toExcuseV2DTO(EXCUSE);

        Excuse savedExcuse = excuseCaptor.getValue();

        /// Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(savedExcuse.getCreatedBy());
        assertEquals(MESSAGE, result.getExcuseMessage());
        assertEquals(CATEGORY, result.getCategory());
        assertNull(savedExcuse.getApprovedBy());
        assertEquals(ADMIN_USER.getUsername(), savedExcuse.getCreatedBy().getUsername());
    }

    @Test
    public void testCreateExcuse_AuthenticationNull() {
        ///  Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(null);

        /// Act
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO)
        );

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(0)).getName();

        /// Assert
        assertNotNull(thrown);
        assertEquals("Authentication is null or empty", thrown.getMessage());
    }

    @Test
    public void testCreateExcuse_AuthenticationGetNameNull() {
        ///  Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        // I don't know who in their right mind would check the test comments when refactoring but
        // the part where we check the auth == null || auth.getName() == null should be
        // auth == null || auth.getName() == "" but well I hope I'll remember to check here
        when(authentication.getName())
                .thenReturn(null);

        /// Act
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO)
        );

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository, times(0)).findByUsername(ADMIN_USER.getUsername());

        /// Assert
        assertNotNull(thrown);
        assertEquals("Authentication is null or empty", thrown.getMessage());
    }

    @Test
    public void testCreateExcuse_UserNotFound() {
        ///  Arrange
        // the ugly not unit testable part... sad developer 😔
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(ADMIN_USER.getUsername());
        when(userRepository.findByUsername(ADMIN_USER.getUsername()))
                .thenReturn(Optional.empty());

        /// Act
        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO)
        );

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository, times(1)).findByUsername(ADMIN_USER.getUsername());
        verify(excuseV2Mapper, times(0)).toExcuse(any(CreateExcuseV2DTO.class));

        /// Assert
        assertNotNull(thrown);
        assertEquals(String.format(UserErrorMessages.USER_NOT_FOUND, ADMIN_USER.getUsername()), thrown.getMessage());
    }
}
