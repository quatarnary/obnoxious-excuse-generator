package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.UserNotAuthorized;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.UserErrorMessages;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class ExcuseV2ServiceDeleteTest {

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;
    @Mock
    UserRepository userRepository;
    @Mock
    ExcuseRepository excuseRepository;
    @InjectMocks
    ExcuseV2Service excuseV2Service;
    private User MOD_USER;
    private Excuse EXCUSE;
    private User SECOND_MOD_USER;
    private User ADMIN_USER;

    @BeforeEach
    void setUp() {
        MOD_USER = User.builder()
                .id(UUID.randomUUID())
                .username("mod-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.MOD)
                .build();
        EXCUSE = Excuse.builder()
                .id(EXCUSE_ID)
                .excuseMessage(MESSAGE)
                .category(CATEGORY)
                .createdBy(MOD_USER)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        SECOND_MOD_USER = User.builder()
                .id(UUID.randomUUID())
                .username("imma-be-second-mod-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.MOD)
                .build();
        ADMIN_USER = User.builder()
                .id(UUID.randomUUID())
                .username("admin-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.ADMIN)
                .build();
    }

    @Test
    public void testDeleteExcuse_Mod_CreatedBySelf() {
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
        doNothing().when(excuseRepository).deleteById(EXCUSE_ID);

        /// Act
        excuseV2Service.deleteExcuse(EXCUSE_ID);

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(MOD_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseRepository).deleteById(EXCUSE_ID);

        /// Assert
    }

    @Test
    public void testDeleteExcuse_Mod_Not_CreatedBySelf() {
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
                () -> excuseV2Service.deleteExcuse(EXCUSE_ID)
        );

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(SECOND_MOD_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseRepository, times(0)).deleteById(EXCUSE_ID);

        /// Assert
        assertNotNull(thrown);
        assertEquals(String.format(UserErrorMessages.USER_NOT_AUTHORIZED, SECOND_MOD_USER.getUsername()), thrown.getMessage());
    }

    @Test
    public void testDeleteExcuse_Admin_Not_CreatedBySelf() {
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
        doNothing().when(excuseRepository).deleteById(EXCUSE_ID);

        /// Act
        excuseV2Service.deleteExcuse(EXCUSE_ID);

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(ADMIN_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseRepository).deleteById(EXCUSE_ID);

        /// Assert
    }
}
