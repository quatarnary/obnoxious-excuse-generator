package com.ilyasbugra.excusegenerator.v1;

import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.v1.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.mapper.ExcuseMapper;
import com.ilyasbugra.excusegenerator.v1.service.ExcuseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcuseServiceTest {

    private static final Excuse workExcuseEntity = Excuse.builder()
            .id(1L)
            .excuseMessage("I was abducted by aliens.")
            .category("Work")
            .build();
    private static final Excuse schoolExcuseEntity = Excuse.builder()
            .id(2L)
            .excuseMessage("My dog ate me.")
            .category("School")
            .build();
    @InjectMocks
    private ExcuseService excuseService;
    @Mock
    private ExcuseRepository excuseRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllExcuses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Excuse> excusePage = new PageImpl<>(List.of(workExcuseEntity, schoolExcuseEntity));
        when(excuseRepository.findAll(any(Pageable.class)))
                .thenReturn(excusePage);

        Page<ExcuseDTO> result = excuseService.getAllExcuses(pageable);

        assertEquals(2, result.getContent().size());
        verify(excuseRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetExcuseById() {
        Excuse excuse = workExcuseEntity;
        when(excuseRepository.findById(1L)).thenReturn(Optional.of(excuse));

        ExcuseDTO result = excuseService.getExcuseById(1L);

        assertNotNull(result);
        assertEquals(workExcuseEntity.getCategory(), result.getCategory());
        verify(excuseRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetExcuseById_NotFound() {
        when(excuseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ExcuseNotFoundException.class, () -> excuseService.getExcuseById(1L));
        verify(excuseRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRandomExcuse() {
        Pageable pageable = PageRequest.of(0, 1);
        when(excuseRepository.count()).thenReturn(2L);
        when(excuseRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(schoolExcuseEntity)));

        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(2)).thenReturn(0);

        ExcuseService mockExcuseServiceWithRandom = new ExcuseService(excuseRepository, mockRandom);
        ExcuseDTO result = mockExcuseServiceWithRandom.getRandomExcuse();

        assertNotNull(result);
        assertEquals(schoolExcuseEntity.getCategory(), result.getCategory());
        assertEquals(schoolExcuseEntity.getExcuseMessage(), result.getExcuseMessage());

        verify(excuseRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetExcusesByCategory() {
        String category = "Work";
        Pageable pageable = PageRequest.of(0, 10);
        when(excuseRepository.findByCategoryStartingWithIgnoreCase(category, pageable))
                .thenReturn(new PageImpl<>(List.of(workExcuseEntity, workExcuseEntity)));

        Page<ExcuseDTO> result = excuseService.getExcusesByCategory(category, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(workExcuseEntity.getCategory(), result.getContent().getFirst().getCategory());

        verify(excuseRepository, times(1)).findByCategoryStartingWithIgnoreCase(category, pageable);
    }

    @Test
    public void testGetExcusesByCategory_NotFound() {
        String category = "NonExistent";
        Pageable pageable = PageRequest.of(0, 10);

        when(excuseRepository.findByCategoryStartingWithIgnoreCase(category, pageable))
                .thenReturn(Page.empty());

        Exception exception = assertThrows(ExcuseCategoryNotFoundException.class, () -> excuseService.getExcusesByCategory(category, pageable));

        assertEquals(String.format(ErrorMessages.CATEGORY_NOT_FOUND, category), exception.getMessage());

        verify(excuseRepository, times(1)).findByCategoryStartingWithIgnoreCase(category, pageable);
    }

    @Test
    public void testCreateExcuse() {
        CreateExcuseDTO createExcuseDTO = new CreateExcuseDTO("My dog ate me.", "School");
        Excuse excuse = ExcuseMapper.toExcuse(createExcuseDTO);
        when(excuseRepository.save(excuse)).thenReturn(excuse);

        ExcuseDTO result = excuseService.createExcuse(createExcuseDTO);

        assertNotNull(result);
        assertEquals("School", result.getCategory());
        verify(excuseRepository, times(1)).save(excuse);
    }

    @Test
    public void testUpdateExcuse() {
        Long id = 1L;
        String newCategory = "new category";
        String newExcuse = "new excuse";

        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .excuseMessage(newExcuse)
                .category(newCategory)
                .build();

        Excuse excuse = workExcuseEntity;

        when(excuseRepository.findById(id)).thenReturn(Optional.of(excuse));
        when(excuseRepository.save(excuse)).thenReturn(excuse);

        ExcuseDTO result = excuseService.updateExcuse(id, updateExcuseDTO);

        assertNotNull(result);
        assertEquals(newExcuse, result.getExcuseMessage());
        assertEquals(newCategory, result.getCategory());

        verify(excuseRepository, times(1)).findById(id);
        verify(excuseRepository, times(1)).save(excuse);
    }

    @Test
    public void testDeleteExcuse() {
        Long id = 1L;

        when(excuseRepository.existsById(id)).thenReturn(true);
        doNothing().when(excuseRepository).deleteById(id);

        excuseService.deleteExcuse(id);

        verify(excuseRepository, times(1)).existsById(id);
        verify(excuseRepository, times(1)).deleteById(id);
    }
}
