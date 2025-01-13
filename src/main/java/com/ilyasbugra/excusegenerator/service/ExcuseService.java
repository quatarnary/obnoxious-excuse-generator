package com.ilyasbugra.excusegenerator.service;

import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.mapper.ExcuseMapper;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ExcuseService {

    private final ExcuseRepository excuseRepository;
    private final Random random;

    public ExcuseService(ExcuseRepository excuseRepository, Random random) {
        this.excuseRepository = excuseRepository;
        this.random = random;
    }

    public Page<ExcuseDTO> getAllExcuses(Pageable pageable) {
        return excuseRepository.findAll(pageable)
                .map(ExcuseMapper::toExcuseDTO);
    }

    public ExcuseDTO getExcuseById(Long id) {
        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        return ExcuseMapper.toExcuseDTO(excuse);
    }

    // TODO: With paging this setup has to be more efficient but i still have my doubts on whether using findAll (even with paging) is a good approach...
    // ... but 'till I find a better approach we are going with this... at least now we are not going to fetch 1.6M(the count at the time of writing this comment)
    // data from the db..
    public ExcuseDTO getRandomExcuse() {
        final int pageSize = 1;
        long count = excuseRepository.count();
        if (count == 0) throw new ExcuseNotFoundException(0L);

        // for the random we don't need the (count / pageSize) cast but this is the general usage and I want to get used seeing this...
        int randomPage = random.nextInt((int) Math.ceil((double) count / pageSize));
        Page<Excuse> page = excuseRepository.findAll(PageRequest.of(randomPage, pageSize));

        if (!page.hasContent()) throw new ExcuseNotFoundException(0L);
        return ExcuseMapper.toExcuseDTO(page.getContent().getFirst());
    }

    public Page<ExcuseDTO> getExcusesByCategory(String category, Pageable pageable) {
        Page<Excuse> excusePage = excuseRepository.findByCategoryStartingWithIgnoreCase(category, pageable);

        if (excusePage.isEmpty()) throw new ExcuseCategoryNotFoundException(category);

        return excusePage.map(ExcuseMapper::toExcuseDTO);
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
