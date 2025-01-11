package com.ilyasbugra.excusegenerator;

import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.mapper.ExcuseMapper;
import com.ilyasbugra.excusegenerator.model.Excuse;
import com.ilyasbugra.excusegenerator.repository.ExcuseRepository;
import com.ilyasbugra.excusegenerator.service.ExcuseService;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcuseServiceTest {

    private static final Excuse excuseEntity1 = Excuse.builder()
            .id(1L)
            .excuseMessage("I was abducted by aliens.")
            .category("Work")
            .build();
    private static final Excuse excuseEntity2 = Excuse.builder()
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
        List<Excuse> excuses = List.of(excuseEntity1, excuseEntity2);
        when(excuseRepository.findAll()).thenReturn(excuses);

        List<ExcuseDTO> result = excuseService.getAllExcuses();

        assertEquals(2, result.size());
        verify(excuseRepository, times(1)).findAll();
    }

    @Test
    public void testGetExcuseById() {
        Excuse excuse = excuseEntity1;
        when(excuseRepository.findById(1L)).thenReturn(Optional.of(excuse));

        ExcuseDTO result = excuseService.getExcuseById(1L);

        assertNotNull(result);
        assertEquals(excuseEntity1.getCategory(), result.getCategory());
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
        List<Excuse> excuses = List.of(excuseEntity1, excuseEntity2);

        when(excuseRepository.findAll()).thenReturn(excuses);

        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(2)).thenReturn(0);

        ExcuseService mockExcuseServiceWithRandom = new ExcuseService(excuseRepository, mockRandom);
        ExcuseDTO result = mockExcuseServiceWithRandom.getRandomExcuse();

        assertNotNull(result);
        assertEquals(excuses.getFirst().getCategory(), result.getCategory());
        assertEquals(excuses.getFirst().getExcuseMessage(), result.getExcuseMessage());

        verify(excuseRepository, times(1)).findAll();
    }

    @Test
    public void testGetExcusesByCategory() {
        String category = "Work";
        List<Excuse> excuses = List.of(excuseEntity1, excuseEntity1);
        when(excuseRepository.findByCategoryStartingWithIgnoreCase(category)).thenReturn(excuses);

        List<ExcuseDTO> result = excuseService.getExcusesByCategory(category);

        assertEquals(2, result.size());
        assertEquals(excuses.get(0).getCategory(), result.get(0).getCategory());
        assertEquals(excuses.get(1).getCategory(), result.get(1).getCategory());

        verify(excuseRepository, times(1)).findByCategoryStartingWithIgnoreCase(category);
    }

    @Test
    public void testGetExcusesByCategory_NotFound() {
        String category = "NonExistent";

        Exception exception = assertThrows(ExcuseCategoryNotFoundException.class, () -> excuseService.getExcusesByCategory(category));

        assertEquals(String.format(ErrorMessages.CATEGORY_NOT_FOUND, category), exception.getMessage());

        verify(excuseRepository, times(1)).findByCategoryStartingWithIgnoreCase(category);
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

        Excuse excuse = excuseEntity1;

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
