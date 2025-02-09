package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.actions.admin.AdminUser;
import com.ilyasbugra.excusegenerator.actions.mod.ModUser;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.model.UserRole;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.util.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    UserHelper userHelper;
    @Mock
    ModUser modUser;
    @Mock
    AdminUser adminUser;
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
        when(userHelper.getAuthenticatedUser())
                .thenReturn(ADMIN_USER);
        when(excuseV2Mapper.toExcuse(CREATE_EXCUSE_V2_DTO))
                .thenReturn(EXCUSE);
        doNothing().when(adminUser).createExcuse(EXCUSE, ADMIN_USER);
        when(excuseRepository.save(EXCUSE))
                .thenReturn(EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.createExcuse(CREATE_EXCUSE_V2_DTO);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseV2Mapper).toExcuse(CREATE_EXCUSE_V2_DTO);
        verify(adminUser).createExcuse(EXCUSE, ADMIN_USER);
        verify(excuseRepository).save(EXCUSE);
        verify(excuseV2Mapper).toExcuseV2DTO(EXCUSE);

        /// Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getUpdatedAt());
        assertEquals(MESSAGE, result.getExcuseMessage());
        assertEquals(CATEGORY, result.getCategory());
    }
}
