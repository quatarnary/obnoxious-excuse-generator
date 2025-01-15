package com.ilyasbugra.excusegenerator.v2.service;

import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.v2.dto.CreateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.ExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.dto.UpdateExcuseV2DTO;
import com.ilyasbugra.excusegenerator.v2.mapper.ExcuseV2Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ExcuseV2Service {

    private final ExcuseRepository excuseRepository;
    private final Random random;
    private final ExcuseV2Mapper excuseV2Mapper;

    public ExcuseV2Service(ExcuseRepository excuseRepository, Random random, ExcuseV2Mapper excuseV2Mapper) {
        this.excuseRepository = excuseRepository;
        this.random = random;
        this.excuseV2Mapper = excuseV2Mapper;
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
        Excuse excuse = excuseV2Mapper.toExcuse(createExcuseV2DTO);
        Excuse savedExcuse = excuseRepository.save(excuse);
        return excuseV2Mapper.toExcuseV2DTO(savedExcuse);
    }

    public ExcuseV2DTO updateExcuse(Long id, UpdateExcuseV2DTO updateExcuseV2DTO) {
        Excuse excuse = excuseRepository.findById(id)
                .orElseThrow(() -> new ExcuseNotFoundException(id));

        excuseV2Mapper.updateExcuseV2(updateExcuseV2DTO, excuse);
        Excuse updatedExcuse = excuseRepository.save(excuse);
        return excuseV2Mapper.toExcuseV2DTO(updatedExcuse);
    }

    public void deleteExcuse(Long id) {
        if (!excuseRepository.existsById(id)) {
            throw new ExcuseNotFoundException(id);
        }

        excuseRepository.deleteById(id);
    }
}
