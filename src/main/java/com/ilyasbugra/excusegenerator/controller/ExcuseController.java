package com.ilyasbugra.excusegenerator.controller;

import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.service.ExcuseService;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/excuses")
public class ExcuseController {

    private final ExcuseService excuseService;

    public ExcuseController(ExcuseService excuseService) {
        this.excuseService = excuseService;
    }

    @GetMapping
    public List<ExcuseDTO> getAllExcuses() {
        return excuseService.getAllExcuses();
    }

    @GetMapping("/{id}")
    public ExcuseDTO getExcuseById(@PathVariable Long id) {
        return excuseService.getExcuseById(id);
    }

    @GetMapping("/random")
    public ExcuseDTO getRandomExcuse() {
        return excuseService.getRandomExcuse();
    }

    @GetMapping("/category/{category}")
    public List<ExcuseDTO> getExcusesByCategory(@PathVariable String category) {
        validateCategory(category);
        return excuseService.getExcusesByCategory(category);
    }

    @PostMapping
    public ExcuseDTO createExcuse(@Valid @RequestBody CreateExcuseDTO createExcuseDTO) {
        return excuseService.createExcuse(createExcuseDTO);
    }

    @PutMapping("/{id}")
    public ExcuseDTO updateExcuse(@PathVariable Long id, @Valid @RequestBody UpdateExcuseDTO updateExcuseDTO) {
        return excuseService.updateExcuse(id, updateExcuseDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteExcuse(@PathVariable Long id) {
        excuseService.deleteExcuse(id);
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidInputException(ErrorMessages.EMPTY_CATEGORY);
        }
        if (category.length() > 50) {
            throw new InvalidInputException(ErrorMessages.LARGE_CATEGORY);
        }
    }
}
