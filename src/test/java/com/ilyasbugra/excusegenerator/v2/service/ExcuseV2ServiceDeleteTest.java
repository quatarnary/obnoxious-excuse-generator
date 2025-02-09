package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.actions.admin.AdminUser;
import com.ilyasbugra.excusegenerator.actions.mod.ModUser;
import com.ilyasbugra.excusegenerator.exception.UserNotAuthorized;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.model.UserRole;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.UserErrorMessages;
import com.ilyasbugra.excusegenerator.v2.helper.ExcuseHelper;
import com.ilyasbugra.excusegenerator.v2.helper.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceDeleteTest {

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";
    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    UserHelper userHelper;
    @Mock
    ExcuseHelper excuseHelper;
    @Mock
    ModUser modUser;
    @Mock
    AdminUser adminUser;
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
        when(userHelper.getAuthenticatedUser())
                .thenReturn(MOD_USER);
        when(excuseHelper.getExcuseById(EXCUSE_ID))
                .thenReturn(EXCUSE);
        doAnswer(invocation -> true)
                .when(modUser).deleteExcuse(EXCUSE, MOD_USER);
        doNothing()
                .when(excuseRepository).deleteById(EXCUSE_ID);

        /// Act
        excuseV2Service.deleteExcuse(EXCUSE_ID);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseHelper).getExcuseById(EXCUSE_ID);
        verify(modUser).deleteExcuse(EXCUSE, MOD_USER);
        verify(excuseRepository).deleteById(EXCUSE_ID);

        /// Assert
    }

    @Test
    public void testDeleteExcuse_Mod_Not_CreatedBySelf() {
        /// Arrange
        when(userHelper.getAuthenticatedUser())
                .thenReturn(SECOND_MOD_USER);
        when(excuseHelper.getExcuseById(EXCUSE_ID))
                .thenReturn(EXCUSE);
        doAnswer(invocation -> false)
                .when(modUser).deleteExcuse(EXCUSE, SECOND_MOD_USER);
        /// Act
        UserNotAuthorized thrown = assertThrows(
                UserNotAuthorized.class,
                () -> excuseV2Service.deleteExcuse(EXCUSE_ID)
        );

        verify(userHelper).getAuthenticatedUser();
        verify(excuseHelper).getExcuseById(EXCUSE_ID);
        verify(modUser).deleteExcuse(EXCUSE, SECOND_MOD_USER);
        verify(excuseRepository, times(0)).deleteById(EXCUSE_ID);

        /// Assert
        assertNotNull(thrown);
        assertEquals(String.format(UserErrorMessages.USER_NOT_AUTHORIZED, SECOND_MOD_USER.getUsername()), thrown.getMessage());
    }

    @Test
    public void testDeleteExcuse_Admin_Not_CreatedBySelf() {
        /// Arrange
        when(userHelper.getAuthenticatedUser())
                .thenReturn(ADMIN_USER);
        when(excuseHelper.getExcuseById(EXCUSE_ID))
                .thenReturn(EXCUSE);
        doAnswer(invocation -> true)
                .when(adminUser).deleteExcuse();
        doNothing()
                .when(excuseRepository).deleteById(EXCUSE_ID);

        /// Act
        excuseV2Service.deleteExcuse(EXCUSE_ID);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseHelper).getExcuseById(EXCUSE_ID);
        verify(adminUser).deleteExcuse();
        verify(excuseRepository).deleteById(EXCUSE_ID);

        /// Assert
    }
}
