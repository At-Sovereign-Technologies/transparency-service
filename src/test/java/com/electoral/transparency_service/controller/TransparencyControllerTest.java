package com.electoral.transparency_service.controller;

// ============================================================
//  TIPO: Integración (MockMvc) — TransparencyController
//  Atributos: Compatibilidad (EQ-8) | Usabilidad (EQ-12)
//             Seguridad (EQ-19)
// ============================================================

import com.electoral.transparency_service.dto.RecordResponse;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.exception.ResourceNotFoundException;
import com.electoral.transparency_service.service.TransparencyService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransparencyController.class)
@DisplayName("TransparencyController — Integración MockMvc")
class TransparencyControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private TransparencyService service;

    // C-01 | EQ-8 | Retorna 200 con JSON válido
    @Test @DisplayName("C-01 | EQ-8 | Retorna 200 con JSON válido para elección existente")
    void should_return200_when_electionHasRecords() throws Exception {
        TransparencyResponse response = TransparencyResponse.builder()
                .electionId(1L)
                .records(List.of(new RecordResponse("VOTO_EMITIDO", "Voto ok", LocalDateTime.now())))
                .build();

        when(service.getRecords(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/transparency").param("electionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.electionId").value(1))
                .andExpect(jsonPath("$.records.length()").value(1));
    }

    // C-02 | EQ-19 | Retorna 404 para elección sin registros
    @Test @DisplayName("C-02 | EQ-19 | Retorna 404 para elección sin registros")
    void should_return404_when_noRecordsFound() throws Exception {
        when(service.getRecords(999L)).thenThrow(new ResourceNotFoundException("No records found"));

        mockMvc.perform(get("/api/v1/transparency").param("electionId", "999"))
                .andExpect(status().isNotFound());
    }

    // U-01 | EQ-12 | Rechaza electionId con letras
    @Test @DisplayName("U-01 | EQ-12 | Rechaza electionId con letras")
    void should_return400_when_electionIdHasLetters() throws Exception {
        mockMvc.perform(get("/api/v1/transparency").param("electionId", "ABC"))
                .andExpect(status().isBadRequest());
    }

    // U-02 | EQ-12 | Rechaza cuando no se envía electionId
    @Test @DisplayName("U-02 | EQ-12 | Retorna 400 cuando no se envía el parámetro electionId")
    void should_return400_when_paramIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/transparency"))
                .andExpect(status().isBadRequest());
    }

    // U-03 | EQ-12 | Rechaza electionId con caracteres especiales
    @Test @DisplayName("U-03 | EQ-12 | Rechaza electionId con caracteres especiales")
    void should_return400_when_electionIdHasSpecialChars() throws Exception {
        mockMvc.perform(get("/api/v1/transparency").param("electionId", "1-2"))
                .andExpect(status().isBadRequest());
    }
}
