package com.ilyasbugra.excusegenerator.v1.controller;

import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.v1.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.service.ExcuseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/excuses")
public class ExcuseController {

    public static final Logger logger = LoggerFactory.getLogger(ExcuseController.class);
    private final HttpServletRequest request;
    private final ExcuseService excuseService;

    public ExcuseController(HttpServletRequest request, ExcuseService excuseService) {
        this.request = request;
        this.excuseService = excuseService;
    }

    @GetMapping
    public ResponseEntity<Page<ExcuseDTO>> getAllExcuses(Pageable pageable) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (pageable.getPageSize() > 100) throw new InvalidInputException(ErrorMessages.LARGE_PAGEABLE_SIZE);

        Page<ExcuseDTO> excuses = excuseService.getAllExcuses(pageable);

        logger.info("Fetched {} excuses (page {} of {})", excuses.getContent().size(), excuses.getNumber(), excuses.getTotalPages());

        return ResponseEntity.ok(excuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExcuseDTO> getExcuseById(@PathVariable Long id) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        ExcuseDTO excuse = excuseService.getExcuseById(id);

        logger.info("Fetched excuse with id {}", id);

        return ResponseEntity.ok(excuse);
    }

    @GetMapping("/random")
    public ResponseEntity<ExcuseDTO> getRandomExcuse() {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        ExcuseDTO excuse = excuseService.getRandomExcuse();

        logger.info("Fetched random excuse with id {}", excuse.getId());

        return ResponseEntity.ok(excuse);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ExcuseDTO>> getExcusesByCategory(
            @RequestParam("category") @NotBlank String category,
            Pageable pageable
    ) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (pageable.getPageSize() > 100) throw new InvalidInputException(ErrorMessages.LARGE_PAGEABLE_SIZE);

        Page<ExcuseDTO> excuses = excuseService.getExcusesByCategory(category, pageable);

        logger.info("Fetched {} excuses by category {} (page {} of {})",
                excuses.getContent().size(), category, excuses.getNumber(), excuses.getTotalPages());

        return ResponseEntity.ok(excuses);
    }

    @PostMapping
    public ResponseEntity<ExcuseDTO> createExcuse(@Valid @RequestBody CreateExcuseDTO createExcuseDTO) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Creating excuse {}", createExcuseDTO);

        ExcuseDTO excuse = excuseService.createExcuse(createExcuseDTO);

        logger.info("Created excuse {}", excuse);

        return ResponseEntity
                .created(URI.create("/api/v1/excuses/" + excuse.getId()))
                .body(excuse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExcuseDTO> updateExcuse(@PathVariable Long id, @Valid @RequestBody UpdateExcuseDTO updateExcuseDTO) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Updating excuse with id {}", id);

        ExcuseDTO excuse = excuseService.updateExcuse(id, updateExcuseDTO);

        logger.info("Updated excuse with id {} to excuse {}", id, excuse);

        return ResponseEntity.ok(excuse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExcuse(@PathVariable Long id) {
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Deleting excuse with id {}", id);

        excuseService.deleteExcuse(id);

        logger.info("Deleted excuse with id {}", id);

        return ResponseEntity.noContent().build();
    }
}
