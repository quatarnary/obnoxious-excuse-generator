package com.ilyasbugra.excusegenerator.service;

import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
import com.ilyasbugra.excusegenerator.mapper.ExcuseMapper;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ExcuseService {

    private final ExcuseRepository excuseRepository;
    private final Random random = new Random();

    public ExcuseService(ExcuseRepository excuseRepository) {
        this.excuseRepository = excuseRepository;
    }

    public List<ExcuseDTO> getAllExcuses() {
        return excuseRepository.findAll()
                .stream()
                .map(ExcuseMapper::toExcuseDTO)
                .collect(Collectors.toList());
    }

    public ExcuseDTO getExcuseById(Long id) {
        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        return ExcuseMapper.toExcuseDTO(excuse);
    }

    public ExcuseDTO getRandomExcuse() {
        List<Excuse> allExcuses = excuseRepository.findAll();
        if (allExcuses.isEmpty()) {
            throw new ExcuseNotFoundException(0L);
        }
        return ExcuseMapper.toExcuseDTO(allExcuses.get(random.nextInt(allExcuses.size())));
    }

    public List<ExcuseDTO> getExcusesByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidInputException(ErrorMessages.EMPTY_CATEGORY);
        }
        if (category.length() > 50) {
            throw new InvalidInputException(ErrorMessages.LARGE_CATEGORY);
        }

        List<Excuse> allExcuses = excuseRepository.findByCategoryIgnoreCase(category);
        if (allExcuses.isEmpty()) {
            throw new ExcuseCategoryNotFoundException(category);
        }

        return allExcuses.stream()
                .map(ExcuseMapper::toExcuseDTO)
                .collect(Collectors.toList());
    }

    public ExcuseDTO createExcuse(CreateExcuseDTO createExcuseDTO) {
        Excuse excuse = ExcuseMapper.toExcuse(createExcuseDTO);
        Excuse savedExcuse = excuseRepository.save(excuse);
        return ExcuseMapper.toExcuseDTO(savedExcuse);
    }

    public ExcuseDTO updateExcuse(Long id, UpdateExcuseDTO updateExcuseDTO) {
        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        ExcuseMapper.updateExcuse(updateExcuseDTO, excuse);
        Excuse updatedExcuse = excuseRepository.save(excuse);
        return ExcuseMapper.toExcuseDTO(updatedExcuse);
    }

    public void deleteExcuse(Long id) {
        if (!excuseRepository.existsById(id)) {
            throw new ExcuseNotFoundException(id);
        }

        excuseRepository.deleteById(id);
    }
}
