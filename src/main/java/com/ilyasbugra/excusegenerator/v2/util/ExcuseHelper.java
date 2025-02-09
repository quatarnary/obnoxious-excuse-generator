package com.ilyasbugra.excusegenerator.v2.util;

import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ExcuseHelper {

    private final ExcuseRepository excuseRepository;

    public ExcuseHelper(ExcuseRepository excuseRepository) {
        this.excuseRepository = excuseRepository;
    }

    public Excuse getExcuseById(Long id) {
        return excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));
    }

    public Excuse findByIdIncludingUnapproved(Long id) {
        return excuseRepository.findByIdIncludingUnapproved(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));
    }

    public Excuse getRandomExcuse() {
        Excuse excuse = excuseRepository.findRandomExcuse();
        if (excuse == null) throw new ExcuseNotFoundException(0L);
        return excuse;
    }

    public Page<Excuse> getExcusesByCategory(String category, Pageable pageable) {
        Page<Excuse> excusePage = excuseRepository.findByCategoryStartingWithIgnoreCase(category, pageable);
        if (excusePage.isEmpty()) throw new ExcuseCategoryNotFoundException(category);
        return excusePage;
    }
}
