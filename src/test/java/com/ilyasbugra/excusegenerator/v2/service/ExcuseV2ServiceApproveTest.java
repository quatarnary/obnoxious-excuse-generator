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
public class ExcuseV2ServiceApproveTest {

    public static final UUID ADMIN_USER_ID = UUID.randomUUID();
    public static final UUID MOD_USER_ID = UUID.randomUUID();

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";

    private static final User ADMIN_USER = User.builder()
            .id(ADMIN_USER_ID)
            .username("admin-lololololol")
            .password("password-go-brrrrrr")
            .userRole(UserRole.ADMIN)
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
    Authentication authentication;
    @Mock
    SecurityContext securityContext;
    @Mock
    UserRepository userRepository;
    @Mock
    ExcuseRepository excuseRepository;
    @Mock
    ExcuseV2Mapper excuseV2Mapper;

    @InjectMocks
    ExcuseV2Service excuseV2Service;

    @Test
    public void testApprove_Admin_Not_CreatedBySelf() {
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

        ArgumentCaptor<Excuse> excuseCaptor = ArgumentCaptor.forClass(Excuse.class);

        when(excuseRepository.save(any(Excuse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(excuseV2Mapper.toExcuseV2DTO(any(Excuse.class)))
                .thenReturn(EXCUSE_V2_DTO);

        /// Act
        ExcuseV2DTO result = excuseV2Service.approveExcuse(EXCUSE_ID);

        verify(SecurityContextHolder.getContext()).getAuthentication();
        verify(authentication, times(2)).getName();
        verify(userRepository).findByUsername(ADMIN_USER.getUsername());
        verify(excuseRepository).findById(EXCUSE_ID);
        verify(excuseRepository).save(excuseCaptor.capture());
        verify(excuseV2Mapper).toExcuseV2DTO(any(Excuse.class));

        Excuse approvedExcuse = excuseCaptor.getValue();

        /// Assert
        assertNotNull(result);
        assertEquals(ADMIN_USER.getId(), approvedExcuse.getApprovedBy().getId());
        assertNotEquals(ADMIN_USER.getId(), approvedExcuse.getCreatedBy().getId());
    }
}
