package com.ilyasbugra.excusegenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyasbugra.excusegenerator.controller.ExcuseController;
import com.ilyasbugra.excusegenerator.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.service.ExcuseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExcuseController.class)
public class ExcuseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExcuseService excuseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllExcuses() throws Exception {
        List<ExcuseDTO> excuses = Arrays.asList(
                new ExcuseDTO(1L, "My boss abducted me.", "Work"),
                new ExcuseDTO(2L, "Aliens stole my homework.", "School")
        );

        when(excuseService.getAllExcuses()).thenReturn(excuses);

        mockMvc.perform(get("/api/v1/excuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].category").value("Work"))
                .andExpect(jsonPath("$[1].excuseMessage").value("Aliens stole my homework."));
    }

    @Test
    void testGetExcuseById() throws Exception {
        ExcuseDTO excuseDTO = new ExcuseDTO(1L, "I got locked in the office.", "Work");
        when(excuseService.getExcuseById(1L)).thenReturn(excuseDTO);

        mockMvc.perform(get("/api/v1/excuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.excuseMessage").value("I got locked in the office."));
    }

    @Test
    void testGetExcuseById_NotFound() throws Exception {
        when(excuseService.getExcuseById(1L)).thenThrow(new ExcuseNotFoundException(1L));

        mockMvc.perform(get("/api/v1/excuses/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Excuse with ID " + 1L + " not found."));
    }

    @Test
    void testGetExcusesByCategory_CaseInsensitive() throws Exception {
        List<ExcuseDTO> excuses = List.of(
                new ExcuseDTO(1L, "My boss abducted me.", "Work")
        );

        when(excuseService.getExcusesByCategory("Work")).thenReturn(excuses);

        mockMvc.perform(get("/api/v1/excuses/category/Work"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].category").value("Work"));
    }

    @Test
    void testCreateExcuse() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("There was a problem with quantum entanglement")
                .build();

        ExcuseDTO excuseDTO = ExcuseDTO.builder()
                .id(1L)
                .category("Work")
                .excuseMessage("There was a problem with quantum entanglement")
                .build();
        when(excuseService.createExcuse(any())).thenReturn(excuseDTO);

        mockMvc.perform(post("/api/v1/excuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.excuseMessage").value("There was a problem with quantum entanglement"));
    }

    @Test
    void testUpdateExcuse() throws Exception {
        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("Updated excuse.")
                .build();

        ExcuseDTO excuseDTO = ExcuseDTO.builder()
                .id(1L)
                .category("Work")
                .excuseMessage("Updated excuse.")
                .build();
        when(excuseService.updateExcuse(1L, updateExcuseDTO)).thenReturn(excuseDTO);

        mockMvc.perform(put("/api/v1/excuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExcuseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.excuseMessage").value("Updated excuse."));
    }

    @Test
    void testDeleteExcuse() throws Exception {
        doNothing().when(excuseService).deleteExcuse(1L);

        mockMvc.perform(delete("/api/v1/excuses/1"))
                .andExpect(status().isOk());

        verify(excuseService, times(1)).deleteExcuse(1L);
    }
}
