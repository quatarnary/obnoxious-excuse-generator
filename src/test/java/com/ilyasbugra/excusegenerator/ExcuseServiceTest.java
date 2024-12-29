package com.ilyasbugra.excusegenerator;

import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseCategoryNotFoundException;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.exception.InvalidInputException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcuseServiceTest {

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
        List<Excuse> excuses = new ArrayList<>();
        excuses.add(new Excuse(1L, "I was abducted by aliens.", "Work"));
        excuses.add(new Excuse(2L, "My dog ate me.", "School"));
        when(excuseRepository.findAll()).thenReturn(excuses);

        List<ExcuseDTO> result = excuseService.getAllExcuses();

        assertEquals(2, result.size());
        verify(excuseRepository, times(1)).findAll();
    }

    @Test
    public void testGetExcuseById() {
        Excuse excuse = new Excuse(1L, "My dog ate me.", "School");
        when(excuseRepository.findById(1L)).thenReturn(Optional.of(excuse));

        ExcuseDTO result = excuseService.getExcuseById(1L);

        assertNotNull(result);
        assertEquals("School", result.getCategory());
        verify(excuseRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetExcuseById_NotFound() {
        when(excuseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ExcuseNotFoundException.class, () -> excuseService.getExcuseById(1L));
        verify(excuseRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetExcusesByCategory() {
        String category = "Personal";
        List<Excuse> excuses = new ArrayList<>();
        excuses.add(new Excuse(1L, "I overslept.", category));
        excuses.add(new Excuse(2L, "My car broke down.", category));
        when(excuseRepository.findByCategoryIgnoreCase(category)).thenReturn(excuses);

        List<ExcuseDTO> result = excuseService.getExcusesByCategory(category);

        assertEquals(2, result.size());
        assertEquals("I overslept.", result.get(0).getExcuseMessage());
        assertEquals("My car broke down.", result.get(1).getExcuseMessage());

        verify(excuseRepository, times(1)).findByCategoryIgnoreCase(category);
    }

    @Test
    public void testGetExcusesByCategory_NotFound() {
        String category = "NonExistent";
        List<Excuse> excuses = new ArrayList<>();
        when(excuseRepository.findByCategoryIgnoreCase(category)).thenReturn(excuses);

        Exception exception = assertThrows(ExcuseCategoryNotFoundException.class, () -> excuseService.getExcusesByCategory(category));

        assertEquals(String.format(ErrorMessages.CATEGORY_NOT_FOUND, category), exception.getMessage());

        verify(excuseRepository, times(1)).findByCategoryIgnoreCase(category);
    }

    @Test
    public void testGetExcusesByCategory_EmptyInput() {
        String category = "";

        Exception exception = assertThrows(InvalidInputException.class, () -> excuseService.getExcusesByCategory(category));

        assertEquals(String.format(ErrorMessages.EMPTY_CATEGORY, category), exception.getMessage());
    }

    @Test
    public void testGetExcusesByCategory_LargeInput() {
        String category = "loingloingloing loing loinng loin gloking liognnk loingk";

        Exception exception = assertThrows(InvalidInputException.class, () -> excuseService.getExcusesByCategory(category));

        assertEquals(String.format(ErrorMessages.LARGE_CATEGORY, category), exception.getMessage());
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
}
