package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.exception.UserNotAuthorized;
import com.ilyasbugra.excusegenerator.exception.UserNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import com.ilyasbugra.excusegenerator.v2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ExcuseV2Service {

    private final ExcuseRepository excuseRepository;
    private final Random random;
    private final ExcuseV2Mapper excuseV2Mapper;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(ExcuseV2Service.class);

    public ExcuseV2Service(ExcuseRepository excuseRepository, Random random, ExcuseV2Mapper excuseV2Mapper, UserRepository userRepository) {
        this.excuseRepository = excuseRepository;
        this.random = random;
        this.excuseV2Mapper = excuseV2Mapper;
        this.userRepository = userRepository;
    }

    public Page<ExcuseV2DTO> getAllExcuses(Pageable pageable) {
        return excuseRepository.findAll(pageable)
                .map(excuseV2Mapper::toExcuseV2DTO);
    }

    public ExcuseV2DTO getExcuseById(Long id) {
        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        return excuseV2Mapper.toExcuseV2DTO(excuse);
    }

    // TODO: With paging this setup has to be more efficient but i still have my doubts on whether using findAll (even with paging) is a good approach...
    // ... but 'till I find a better approach we are going with this... at least now we are not going to fetch 1.6M(the count at the time of writing this comment)
    // data from the db..
    public ExcuseV2DTO getRandomExcuse() {
        final int pageSize = 1;
        long count = excuseRepository.count();
        if (count == 0) throw new ExcuseNotFoundException(0L);

        // for the random we don't need the (count / pageSize) cast but this is the general usage and I want to get used seeing this...
        int randomPage = random.nextInt((int) Math.ceil((double) count / pageSize));
        Page<Excuse> page = excuseRepository.findAll(PageRequest.of(randomPage, pageSize));

        if (!page.hasContent()) throw new ExcuseNotFoundException(0L);
        return excuseV2Mapper.toExcuseV2DTO(page.getContent().getFirst());
    }

    public Page<ExcuseV2DTO> getExcusesByCategory(String category, Pageable pageable) {
        Page<Excuse> excusePage = excuseRepository.findByCategoryStartingWithIgnoreCase(category, pageable);

        if (excusePage.isEmpty()) throw new ExcuseCategoryNotFoundException(category);

        return excusePage.map(excuseV2Mapper::toExcuseV2DTO);
    }

    public ExcuseV2DTO createExcuse(CreateExcuseV2DTO createExcuseV2DTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            logger.error("Authentication is null or empty: {}", authentication != null ? authentication.getName() : "auth is null");
            throw new IllegalStateException("Authentication is null or empty");
        }
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} not found", username);
                    return new UserNotFoundException(username);
                });

        Excuse excuse = excuseV2Mapper.toExcuse(createExcuseV2DTO);
        excuse.setCreatedBy(user);

        Excuse savedExcuse = excuseRepository.save(excuse);
        return excuseV2Mapper.toExcuseV2DTO(savedExcuse);
    }

    public ExcuseV2DTO updateExcuse(Long id, UpdateExcuseV2DTO updateExcuseV2DTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            logger.error("Authentication is null or empty: {}", authentication != null ? authentication.getName() : "auth is null");
            throw new IllegalStateException("Authentication is null or empty");
        }
        String username = authentication.getName();

        // I'm questioning about whether we should get the user here, or
        // can we just use the username??
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} not found", username);
                    return new UserNotFoundException(username);
                });

        if (user.getUserRole() == UserRole.REGULAR) {
            logger.error("User '{}' is regular user", username);
        }

        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        if (user.getUserRole() == UserRole.MOD
                && !excuse.getCreatedBy().getId().equals(user.getId())) {
            logger.debug("User '{}' tried to update with role '{}'", user.getUsername(), user.getUserRole());
            throw new UserNotAuthorized(user.getUsername());
        }

        // TODO: how did I thought that mapper returning something for one method and not returning anything would be good
        // It really happened, the thing people say about you know what your code is doing now but even you are not going
        // remember what is doing in the future... I was just looking at the line thinking that how did update method was working
        // T-earlier that what I wrote at the end lol
        // I'm from few hours future and the more I look at how this mapper is working the more problem I'm seeing
        // for the sake of my sanity as soon as the update logic ends I'll just change the mapper..
        // T-25-Jan-2025-19:41
        // God why! WHYYYYYY!
        // T-25-jan-2025-21:47
        excuseV2Mapper.updateExcuseV2(updateExcuseV2DTO, excuse);
        // anyway I was already going to change the mapper to also set the updatedBy so I'll fix it when I'm refactoring that part
        excuse.setUpdatedBy(user);
        logger.debug("Updating the excuse to: '{}'", excuse.getExcuseMessage());

        Excuse updatedExcuse = excuseRepository.save(excuse);
        logger.debug("User: '{}' with role: {} updated the excuse with id: '{}'", user.getUsername(), user.getUserRole(), updatedExcuse.getId());
        logger.debug("Excuse updated: {}", updatedExcuse.getId());

        return excuseV2Mapper.toExcuseV2DTO(updatedExcuse);
    }

    // Yes I'm just doing copy-paste and I know it is wrong but this will make also the delete work as intendedish for now
    // the next step is to refactor all of these things, so just bare with me for a sec here
    // t-25-jan-25-21:53
    public void deleteExcuse(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            logger.error("Authentication is null or empty: {}", authentication != null ? authentication.getName() : "auth is null");
            throw new IllegalStateException("Authentication is null or empty");
        }
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} not found", username);
                    return new UserNotFoundException(username);
                });

        if (user.getUserRole() == UserRole.REGULAR) {
            logger.error("User '{}' is regular user", username);
        }

        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        if (user.getUserRole() == UserRole.MOD
                && !excuse.getCreatedBy().getId().equals(user.getId())) {
            logger.debug("User '{}' tried to delete with role '{}'", user.getUsername(), user.getUserRole());
            throw new UserNotAuthorized(user.getUsername());
        }

        excuseRepository.deleteById(id);
    }

    // Yes another copy-paste
    // Yes I'll refactor after this
    // No, I'm not trying to delay it
    // I want to keep the commits organized
    // There is no way I'm going to overlook this novel like comments
    // I mean no way, right? right?
    //  t-28-jan-2025
    // approveExcuse confirmation
    // it is working but currently if an admin approves an already approved excuse, it overwrites it
    // we should prevent that so that we can do one less write operation...
    public ExcuseV2DTO approveExcuse(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            logger.error("Authentication is null or empty: {}", authentication != null ? authentication.getName() : "auth is null");
            throw new IllegalStateException("Authentication is null or empty");
        }
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} not found", username);
                    return new UserNotFoundException(username);
                });

        if (user.getUserRole() != UserRole.ADMIN) {
            logger.error("User '{}' is not an admin user", username);
        }

        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        excuse.setApprovedBy(user);

        Excuse approvedExcuse = excuseRepository.save(excuse);
        logger.debug("User: '{}' with role: {} updated the excuse with id: '{}'", user.getUsername(), user.getUserRole(), approvedExcuse.getId());
        logger.debug("Excuse updated: {}", approvedExcuse.getId());

        return excuseV2Mapper.toExcuseV2DTO(approvedExcuse);
    }
}
