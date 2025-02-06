package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcuseV2ServiceDeleteTest {

    public static final UUID MOD_USER_ID = UUID.randomUUID();

    public static final Long EXCUSE_ID = 0L;
    public static final String MESSAGE = "excuse-message";
    public static final String CATEGORY = "category";

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
}
