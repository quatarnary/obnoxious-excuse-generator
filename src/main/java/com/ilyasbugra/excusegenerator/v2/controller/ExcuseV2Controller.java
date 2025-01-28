package com.ilyasbugra.excusegenerator.v2.controller;

import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.service.ExcuseV2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v2/excuses")
public class ExcuseV2Controller {

    public static final Logger logger = LoggerFactory.getLogger(ExcuseV2Controller.class);
    private final HttpServletRequest request;
    private final ExcuseV2Service excuseV2Service;

    public ExcuseV2Controller(HttpServletRequest request, ExcuseV2Service excuseV2Service) {
        this.request = request;
        this.excuseV2Service = excuseV2Service;
    }

    @GetMapping
    public ResponseEntity<Page<ExcuseV2DTO>> getAllExcuses(Pageable pageable) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (pageable.getPageSize() > 100) throw new InvalidInputException(ErrorMessages.LARGE_PAGEABLE_SIZE);

        Page<ExcuseV2DTO> excuses = excuseV2Service.getAllExcuses(pageable);

        logger.info("Fetched {} excuses (page {} of {})", excuses.getContent().size(), excuses.getNumber(), excuses.getTotalPages());

        return ResponseEntity.ok(excuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcuseV2DTO> getExcuseById(@PathVariable Long id) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        ExcuseV2DTO excuse = excuseV2Service.getExcuseById(id);

        logger.info("Fetched excuse with id {}", id);

        return ResponseEntity.ok(excuse);
    }

    @GetMapping("/random")
    public ResponseEntity<ExcuseV2DTO> getRandomExcuse() {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        ExcuseV2DTO excuse = excuseV2Service.getRandomExcuse();

        logger.info("Fetched random excuse with id {}", excuse.getId());

        return ResponseEntity.ok(excuse);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ExcuseV2DTO>> getExcusesByCategory(
            @RequestParam("category") @NotBlank String category,
            Pageable pageable
    ) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (pageable.getPageSize() > 100) throw new InvalidInputException(ErrorMessages.LARGE_PAGEABLE_SIZE);

        Page<ExcuseV2DTO> excuses = excuseV2Service.getExcusesByCategory(category, pageable);

        logger.info("Fetched {} excuses by category {} (page {} of {})",
                excuses.getContent().size(), category, excuses.getNumber(), excuses.getTotalPages());

        return ResponseEntity.ok(excuses);
    }

    @PreAuthorize("hasAnyAuthority('MOD', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ExcuseV2DTO> createExcuse(@Valid @RequestBody CreateExcuseV2DTO createExcuseV2DTO) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Creating excuse {}", createExcuseV2DTO);

        ExcuseV2DTO excuse = excuseV2Service.createExcuse(createExcuseV2DTO);

        logger.info("Created excuse {}", excuse);

        return ResponseEntity
                .created(URI.create("/api/v2/excuses/" + excuse.getId()))
                .body(excuse);
    }

    @PreAuthorize("hasAnyAuthority('MOD', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ExcuseV2DTO> updateExcuse(@PathVariable Long id, @Valid @RequestBody UpdateExcuseV2DTO updateExcuseV2DTO) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Updating excuse with id {}", id);

        ExcuseV2DTO excuse = excuseV2Service.updateExcuse(id, updateExcuseV2DTO);

        logger.info("Updated excuse with id {} to excuse {}", id, excuse);

        return ResponseEntity.ok(excuse);
    }

    @PreAuthorize("hasAnyAuthority('MOD', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExcuse(@PathVariable Long id) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Deleting excuse with id {}", id);

        excuseV2Service.deleteExcuse(id);

        logger.info("Deleted excuse with id {}", id);

        return ResponseEntity.noContent().build();
    }

    // I'm going to refactor this part and maybe collect all the admin related logic to its own endpoint
    // so bare with me for a while here.. t-28-jan-2025
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/admin/{id}")
    public ResponseEntity<ExcuseV2DTO> approveExcuse(@PathVariable Long id) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Approving excuse with id {}", id);

        ExcuseV2DTO excuse = excuseV2Service.approveExcuse(id);

        logger.info("Approved excuse with id {}", id);

        return ResponseEntity.ok(excuse);
    }
}
