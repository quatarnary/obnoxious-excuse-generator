package com.ilyasbugra.excusegenerator.v2.util;

import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
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

    public Excuse getRandomExcuse() {
        Excuse excuse = excuseRepository.findRandomExcuse();
        if (excuse == null) throw new ExcuseNotFoundException(0L);
        return excuse;
    }
}
