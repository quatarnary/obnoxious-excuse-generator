package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.actions.admin.AdminUser;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.model.UserRole;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.util.ExcuseHelper;
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
public class ExcuseV2ServiceApproveTest {

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";
    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    ExcuseV2Mapper excuseV2Mapper;
    @Mock
    UserHelper userHelper;
    @Mock
    ExcuseHelper excuseHelper;
    @Mock
    AdminUser adminUser;
    @InjectMocks
    ExcuseV2Service excuseV2Service;
    private User ADMIN_USER;
    private User MOD_USER;
    private Excuse EXCUSE;
    private ExcuseV2DTO EXCUSE_V2_DTO;

    @BeforeEach
    void setUp() {
        ADMIN_USER = User.builder()
                .id(UUID.randomUUID())  // Unique ID per test
                .username("admin-lololololol")
                .password("password-go-brrrrrr")
                .userRole(UserRole.ADMIN)
                .build();

        MOD_USER = User.builder()
                .id(UUID.randomUUID())  // Unique ID per test
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

        EXCUSE_V2_DTO = ExcuseV2DTO.builder()
                .id(EXCUSE.getId())
                .excuseMessage(EXCUSE.getExcuseMessage())
                .category(EXCUSE.getCategory())
                .updatedAt(EXCUSE.getUpdatedAt())
                .build();
    }

    @Test
    public void testApprove_Admin_Not_CreatedBySelf() {
        /// Arrange
        when(userHelper.getAuthenticatedUser())
                .thenReturn(ADMIN_USER);
        when(excuseHelper.getExcuseById(EXCUSE_ID))
                .thenReturn(EXCUSE);
        doNothing()
                .when(adminUser).approveExcuse(EXCUSE, ADMIN_USER);
        when(excuseRepository.save(EXCUSE))
                .thenReturn(EXCUSE);
        when(excuseV2Mapper.toExcuseV2DTO(EXCUSE))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.approveExcuse(EXCUSE_ID);

        verify(userHelper).getAuthenticatedUser();
        verify(excuseHelper).getExcuseById(EXCUSE_ID);
        verify(adminUser).approveExcuse(EXCUSE, ADMIN_USER);
        verify(excuseRepository).save(EXCUSE);
        verify(excuseV2Mapper).toExcuseV2DTO(EXCUSE);

        /// Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(EXCUSE_ID, result.getId());
        assertEquals(MESSAGE, result.getExcuseMessage());
        assertEquals(CATEGORY, result.getCategory());
    }
}
