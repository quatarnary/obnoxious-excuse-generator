package com.ilyasbugra.excusegenerator.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyasbugra.excusegenerator.exception.ExcuseNotFoundException;
import com.ilyasbugra.excusegenerator.util.ErrorMessages;
import com.ilyasbugra.excusegenerator.v1.controller.ExcuseController;
import com.ilyasbugra.excusegenerator.v1.dto.CreateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.ExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.dto.UpdateExcuseDTO;
import com.ilyasbugra.excusegenerator.v1.service.ExcuseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExcuseController.class)
public class ExcuseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExcuseService excuseService;

    private ExcuseDTO excuseDTO;

    @BeforeEach
    public void setup() {
        excuseDTO = ExcuseDTO.builder()
                .id(1L)
                .category("Work")
                .excuseMessage("My boss abducted me.")
                .build();
    }

    @Test
    void testGetAllExcuses_DefaultPage_ShouldReturn200() throws Exception {
        Page<ExcuseDTO> excusePage = new PageImpl<>(
                List.of(
                        ExcuseDTO.builder()
                                .id(1L)
                                .category("Work")
                                .excuseMessage("My boss abducted me.")
                                .build(),
                        ExcuseDTO.builder()
                                .id(2L)
                                .category("Family")
                                .excuseMessage("My dog ate my homework.")
                                .build()
                )
        );

        when(excuseService.getAllExcuses(any(Pageable.class))).thenReturn(excusePage);

        mockMvc.perform(get("/api/v1/excuses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].category").value("Work"))
                .andExpect(jsonPath("$.content[0].excuseMessage").value("My boss abducted me."))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].category").value("Family"))
                .andExpect(jsonPath("$.content[1].excuseMessage").value("My dog ate my homework."));
    }

    @Test
    void testGetAllExcuses_DefaultPage_EmptyList_ShouldReturn200() throws Exception {
        when(excuseService.getAllExcuses(any(Pageable.class))).thenReturn(new PageImpl<>(List.of())); // Empty list

        mockMvc.perform(get("/api/v1/excuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(0)); // Expecting empty array
    }

    @Test
    void testGetExcusesByCategory_ValidCategory_ShouldReturn200() throws Exception {
        String category = "Work";
        Page<ExcuseDTO> excusePage = new PageImpl<>(
                List.of(
                        ExcuseDTO.builder()
                                .id(1L)
                                .category("Work")
                                .excuseMessage("My boss abducted me.")
                                .build(),
                        ExcuseDTO.builder()
                                .id(2L)
                                .category("Work")
                                .excuseMessage("I had to work late.")
                                .build()
                )
        );

        when(excuseService.getExcusesByCategory(eq(category), any(Pageable.class)))
                .thenReturn(excusePage);

        mockMvc.perform(get("/api/v1/excuses/category/Work"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].category").value("Work"))
                .andExpect(jsonPath("$.content[0].excuseMessage").value("My boss abducted me."))
                .andExpect(jsonPath("$.content[1].category").value("Work"))
                .andExpect(jsonPath("$.content[1].excuseMessage").value("I had to work late."));
    }

    @Test
    void testGetExcuseById_ValidId_ShouldReturn200() throws Exception {
        when(excuseService.getExcuseById(1L)).thenReturn(excuseDTO);

        mockMvc.perform(get("/api/v1/excuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.excuseMessage").value("My boss abducted me."));
    }

    @Test
    void testGetExcuseById_NotFound_ShouldReturn404() throws Exception {
        when(excuseService.getExcuseById(1L)).thenThrow(new ExcuseNotFoundException(1L));

        mockMvc.perform(get("/api/v1/excuses/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format(ErrorMessages.EXCUSE_NOT_FOUND, 1L)
                ));
    }

    @Test
    void testCreateExcuse_ValidRequest_ShouldReturn201() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("Valid excuse.")
                .build();

        when(excuseService.createExcuse(any())).thenReturn(excuseDTO);

        mockMvc.perform(post("/api/v1/excuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.excuseMessage").value("My boss abducted me."));
    }

    @Test
    void testCreateExcuse_EmptyCategory_ShouldReturn400() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category("")
                .excuseMessage("My boss abducted me.")
                .build();

        mockMvc.perform(post("/api/v1/excuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.category").value(
                        ErrorMessages.EMPTY_CATEGORY
                ));
    }

    @Test
    void testCreateExcuse_EmptyMessage_ShouldReturn400() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("")
                .build();

        mockMvc.perform(post("/api/v1/excuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.excuseMessage").value(
                        ErrorMessages.EMPTY_EXCUSE_MESSAGE
                ));
    }

    @Test
    void testCreateExcuse_EmptyFields_ShouldReturn400() throws Exception {
        CreateExcuseDTO createExcuseDTO = CreateExcuseDTO.builder()
                .category("")
                .excuseMessage("")
                .build();

        mockMvc.perform(post("/api/v1/excuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createExcuseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.category").value(
                        ErrorMessages.EMPTY_CATEGORY
                ))
                .andExpect(jsonPath("$.fields.excuseMessage").value(
                        ErrorMessages.EMPTY_EXCUSE_MESSAGE
                ));
    }

    @Test
    void testUpdateExcuse_ValidRequest_ShouldReturn200() throws Exception {
        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("Updated excuse.")
                .build();

        when(excuseService.updateExcuse(eq(1L), any(UpdateExcuseDTO.class))).thenReturn(excuseDTO);

        mockMvc.perform(put("/api/v1/excuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExcuseDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateExcuse_NotFound_ShouldReturn404() throws Exception {
        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .category("Work")
                .excuseMessage("Updated excuse.")
                .build();

        doThrow(new ExcuseNotFoundException(999L))
                .when(excuseService).updateExcuse(eq(999L), any(UpdateExcuseDTO.class));

        mockMvc.perform(put("/api/v1/excuses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExcuseDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format(ErrorMessages.EXCUSE_NOT_FOUND, 999L)
                ));
    }

    @Test
    void testUpdateExcuse_EmptyFields_ShouldReturn400() throws Exception {
        UpdateExcuseDTO updateExcuseDTO = UpdateExcuseDTO.builder()
                .category("")
                .excuseMessage("")
                .build();

        mockMvc.perform(put("/api/v1/excuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateExcuseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.category").value(ErrorMessages.EMPTY_CATEGORY))
                .andExpect(jsonPath("$.fields.excuseMessage").value(ErrorMessages.EMPTY_EXCUSE_MESSAGE));
    }

    @Test
    void testDeleteExcuse_ValidRequest_ShouldReturn204() throws Exception {
        doNothing()
                .when(excuseService).deleteExcuse(eq(1L));

        mockMvc.perform(delete("/api/v1/excuses/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void testDeleteExcuse_NotFound_ShouldReturn404() throws Exception {
        doThrow(new ExcuseNotFoundException(999L))
                .when(excuseService).deleteExcuse(eq(999L));

        mockMvc.perform(delete("/api/v1/excuses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        String.format(ErrorMessages.EXCUSE_NOT_FOUND, 999L)
                ));
    }
}
