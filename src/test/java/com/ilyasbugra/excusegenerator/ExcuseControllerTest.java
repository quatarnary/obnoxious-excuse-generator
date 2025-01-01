package com.ilyasbugra.excusegenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyasbugra.excusegenerator.controller.ExcuseController;
import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.service.ExcuseService;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExcuseController.class)
public class ExcuseControllerTest {

    private static final Long WORK_ID_1 = 1L;
    private static final Long WORK_ID_2 = 2L;
    private static final Long SCHOOL_ID_1 = 3L;
    private static final Long NON_EXISTENT_ID_1 = 99L;

    private static final String WORK_CATEGORY = "Work";
    private static final String SCHOOL_CATEGORY = "School";
    private static final String UPDATED_CATEGORY = "Updated";
    private static final String NON_EXISTENT_CATEGORY = "Non Existent";
    private static final String EMPTY_CATEGORY = "";

    private static final String WORK_EXCUSE_MESSAGE_1 = "My boss abducted me.";
    private static final String WORK_EXCUSE_MESSAGE_2 = "Aliens abducted my boss.";
    private static final String SCHOOL_EXCUSE_MESSAGE_1 = "Aliens stole my homework.";
    private static final String UPDATED_EXCUSE_MESSAGE = "Updated boss abducted me.";
    private static final String EMPTY_EXCUSE_MESSAGE = "";

    private static final ExcuseDTO WORK_EXCUSE_1_DTO = ExcuseDTO.builder()
            .id(WORK_ID_1)
            .category(WORK_CATEGORY)
            .excuseMessage(WORK_EXCUSE_MESSAGE_1)
            .build();

    private static final ExcuseDTO WORK_EXCUSE_2_DTO = ExcuseDTO.builder()
            .id(WORK_ID_2)
            .category(WORK_CATEGORY)
            .excuseMessage(WORK_EXCUSE_MESSAGE_2)
            .build();

    private static final ExcuseDTO SCHOOL_EXCUSE_1_DTO = ExcuseDTO.builder()
            .id(SCHOOL_ID_1)
            .category(SCHOOL_CATEGORY)
            .excuseMessage(SCHOOL_EXCUSE_MESSAGE_1)
            .build();

    private static final String URI_BASE = "/api/v1/excuses";

    private static final String PATH_WORK_1_ID = "/" + WORK_ID_1;
    private static final String PATH_WORK_2_ID = "/" + WORK_ID_2;
    private static final String PATH_SCHOOL_1_ID = "/" + SCHOOL_ID_1;
    private static final String PATH_NON_EXISTENT_1_ID = "/" + NON_EXISTENT_ID_1;

    private static final String PATH_WORK_CATEGORY = "/category/" + WORK_CATEGORY;
    private static final String PATH_SCHOOL_CATEGORY = "/category/" + SCHOOL_CATEGORY;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExcuseService excuseService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<ExcuseDTO> DIFFERENT_CATEGORY_EXCUSE_DTOS;

    private List<ExcuseDTO> WORK_CATEGORY_EXCUSE_DTOS;

    @BeforeEach
    void setUp() {
        DIFFERENT_CATEGORY_EXCUSE_DTOS = List.of(
                WORK_EXCUSE_1_DTO,
                WORK_EXCUSE_2_DTO,
                SCHOOL_EXCUSE_1_DTO
        );

        WORK_CATEGORY_EXCUSE_DTOS = List.of(
                WORK_EXCUSE_1_DTO,
                WORK_EXCUSE_2_DTO
        );
    }

    @Test
    void testGetAllExcuses() throws Exception {
        when(excuseService.getAllExcuses()).thenReturn(DIFFERENT_CATEGORY_EXCUSE_DTOS);

        mockMvc.perform(get(URI_BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(DIFFERENT_CATEGORY_EXCUSE_DTOS.size()))
                .andExpect(jsonPath("$[0].category").value(WORK_CATEGORY))
                .andExpect(jsonPath("$[0].excuseMessage").value(WORK_EXCUSE_MESSAGE_1))
                .andExpect(jsonPath("$[1].category").value(WORK_CATEGORY))
                .andExpect(jsonPath("$[1].excuseMessage").value(WORK_EXCUSE_MESSAGE_2))
                .andExpect(jsonPath("$[2].category").value(SCHOOL_CATEGORY))
                .andExpect(jsonPath("$[2].excuseMessage").value(SCHOOL_EXCUSE_MESSAGE_1));
    }

    @Test
    void testGetAllExcuses_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(excuseService.getAllExcuses()).thenReturn(List.of());

        mockMvc.perform(get(URI_BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetExcuseById() throws Exception {
        when(excuseService.getExcuseById(WORK_ID_1)).thenReturn(WORK_EXCUSE_1_DTO);

        mockMvc.perform(get(URI_BASE + PATH_WORK_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(WORK_CATEGORY))
                .andExpect(jsonPath("$.excuseMessage").value(WORK_EXCUSE_MESSAGE_1));
    }

    @Test
    void testGetExcuseById_NotFound() throws Exception {
        when(excuseService.getExcuseById(NON_EXISTENT_ID_1)).thenThrow(new ExcuseNotFoundException(NON_EXISTENT_ID_1));

        mockMvc.perform(get(URI_BASE + PATH_NON_EXISTENT_1_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(ErrorMessages.EXCUSE_NOT_FOUND, NON_EXISTENT_ID_1)));
    }

    @Test
    void testGetExcusesByCategory_CaseInsensitive() throws Exception {
        when(excuseService.getExcusesByCategory(WORK_CATEGORY)).thenReturn(WORK_CATEGORY_EXCUSE_DTOS);

        mockMvc.perform(get(URI_BASE + PATH_WORK_CATEGORY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(WORK_CATEGORY_EXCUSE_DTOS.size()))
                .andExpect(jsonPath("$[0].category").value(WORK_CATEGORY))
                .andExpect(jsonPath("$[1].category").value(WORK_CATEGORY));
    }

    @Test
    void testCreateExcuse() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category(WORK_CATEGORY)
                .excuseMessage(WORK_EXCUSE_MESSAGE_1)
                .build();

        ExcuseDTO excuseDTO = ExcuseDTO.builder()
                .id(WORK_ID_1)
                .category(WORK_CATEGORY)
                .excuseMessage(WORK_EXCUSE_MESSAGE_1)
                .build();
        when(excuseService.createExcuse(createExcuseDTO)).thenReturn(excuseDTO);

        mockMvc.perform(post(URI_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(WORK_CATEGORY))
                .andExpect(jsonPath("$.excuseMessage").value(WORK_EXCUSE_MESSAGE_1));
    }

    @Test
    void testUpdateExcuse() throws Exception {
        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .category(UPDATED_CATEGORY)
                .excuseMessage(UPDATED_EXCUSE_MESSAGE)
                .build();

        ExcuseDTO excuseDTO = ExcuseDTO.builder()
                .id(WORK_ID_1)
                .category(UPDATED_CATEGORY)
                .excuseMessage(UPDATED_EXCUSE_MESSAGE)
                .build();
        when(excuseService.updateExcuse(WORK_ID_1, updateExcuseDTO)).thenReturn(excuseDTO);

        mockMvc.perform(put(URI_BASE + PATH_WORK_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExcuseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value(UPDATED_CATEGORY))
                .andExpect(jsonPath("$.excuseMessage").value(UPDATED_EXCUSE_MESSAGE));
    }

    @Test
    void testDeleteExcuse() throws Exception {
        doNothing().when(excuseService).deleteExcuse(WORK_ID_1);

        mockMvc.perform(delete(URI_BASE + PATH_WORK_1_ID))
                .andExpect(status().isOk());

        verify(excuseService, times(1)).deleteExcuse(WORK_ID_1);
    }
}
