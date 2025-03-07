package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.actions.admin.AdminUser;
import com.ilyasbugra.excusegenerator.actions.mod.ModUser;
import com.ilyasbugra.excusegenerator.exception.UserNotAuthorized;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.helper.ExcuseHelper;
import com.ilyasbugra.excusegenerator.v2.helper.UserHelper;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExcuseV2Service {

    private final ExcuseRepository excuseRepository;
    private final ExcuseV2Mapper excuseV2Mapper;
    private final UserHelper userHelper;
    private final ExcuseHelper excuseHelper;
    private final AdminUser adminUser;
    private final ModUser modUser;

    private final Logger logger = LoggerFactory.getLogger(ExcuseV2Service.class);

    public ExcuseV2Service(ExcuseRepository excuseRepository, ExcuseV2Mapper excuseV2Mapper, UserHelper userHelper, ExcuseHelper excuseHelper, AdminUser adminUser, ModUser modUser) {
        this.excuseRepository = excuseRepository;
        this.excuseV2Mapper = excuseV2Mapper;
        this.userHelper = userHelper;
        this.excuseHelper = excuseHelper;
        this.adminUser = adminUser;
        this.modUser = modUser;
    }

    public Page<ExcuseV2DTO> getAllExcuses(Pageable pageable) {
        return excuseRepository.findAll(pageable)
                .map(excuseV2Mapper::toExcuseV2DTO);
    }

    public ExcuseV2DTO getExcuseById(Long id) {
        Excuse excuse = excuseHelper.getExcuseById(id);
        return excuseV2Mapper.toExcuseV2DTO(excuse);
    }

    public ExcuseV2DTO getRandomExcuse() {
        return excuseV2Mapper.toExcuseV2DTO(excuseHelper.getRandomExcuse());
    }

    public Page<ExcuseV2DTO> getExcusesByCategory(String category, Pageable pageable) {
        return excuseHelper.getExcusesByCategory(category, pageable)
                .map(excuseV2Mapper::toExcuseV2DTO);
    }

    public ExcuseV2DTO createExcuse(CreateExcuseV2DTO createExcuseV2DTO) {
        User user = userHelper.getAuthenticatedUser();
        Excuse excuse = excuseV2Mapper.toExcuse(createExcuseV2DTO);

        switch (user.getUserRole()) {
            case ADMIN -> adminUser.createExcuse(excuse, user);
            case MOD -> modUser.createExcuse(excuse, user);
            default -> throw new UserNotAuthorized(user.getUsername());
        }

        Excuse savedExcuse = excuseRepository.save(excuse);
        return excuseV2Mapper.toExcuseV2DTO(savedExcuse);
    }

    public ExcuseV2DTO updateExcuse(Long id, UpdateExcuseV2DTO updateExcuseV2DTO) {
        User user = userHelper.getAuthenticatedUser();
        Excuse excuse = excuseHelper.findByIdIncludingUnapproved(id);

        boolean canUpdate = switch (user.getUserRole()) {
            case ADMIN -> adminUser.updateExcuse(excuse, user);
            case MOD -> modUser.updateExcuse(excuse, user);
            default -> false;
        };

        if (!canUpdate) {
            throw new UserNotAuthorized(user.getUsername());
        }

        Excuse modifiedExcuse = excuseV2Mapper.updateExcuseV2(updateExcuseV2DTO, excuse);

        Excuse updatedExcuse = excuseRepository.save(modifiedExcuse);
        logger.debug("User: '{}' with role: {} updated the excuse with id: '{}'", user.getUsername(), user.getUserRole(), updatedExcuse.getId());

        return excuseV2Mapper.toExcuseV2DTO(updatedExcuse);
    }

    public void deleteExcuse(Long id) {
        User user = userHelper.getAuthenticatedUser();
        Excuse excuse = excuseHelper.findByIdIncludingUnapproved(id);

        boolean canDelete = switch (user.getUserRole()) {
            case ADMIN -> adminUser.deleteExcuse();
            case MOD -> modUser.deleteExcuse(excuse, user);
            default -> false;
        };

        if (!canDelete) {
            throw new UserNotAuthorized(user.getUsername());
        }

        excuseRepository.deleteById(id);
    }

    public ExcuseV2DTO approveExcuse(Long id) {
        User user = userHelper.getAuthenticatedUser();
        Excuse excuse = excuseHelper.findByIdIncludingUnapproved(id);

        switch (user.getUserRole()) { // still here for consistency.
            case ADMIN -> adminUser.approveExcuse(excuse, user);
            default -> throw new UserNotAuthorized(user.getUsername());
        }

        Excuse approvedExcuse = excuseRepository.save(excuse);
        logger.debug("User: '{}' with role: {} approved the excuse with id: '{}'", user.getUsername(), user.getUserRole(), approvedExcuse.getId());
        logger.debug("Excuse updated: {}", approvedExcuse.getId());

        return excuseV2Mapper.toExcuseV2DTO(approvedExcuse);
    }
}
