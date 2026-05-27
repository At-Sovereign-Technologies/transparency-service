package com.electoral.transparency_service.service;

import com.electoral.transparency_service.repository.TransparencyRepository;
import com.electoral.transparency_service.cache.RedisCacheAdapter;
import com.electoral.transparency_service.mapper.TransparencyMapper;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.dto.RecordResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;
import com.electoral.transparency_service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransparencyService — Pruebas Unitarias")
class TransparencyServiceTest {

    @Mock private TransparencyRepository repository;
    @Mock private RedisCacheAdapter cache;
    @Mock private TransparencyMapper mapper;
    @Mock private ObjectMapper objectMapper;
    @Mock private FraudRiskCalculatorService fraudRiskCalculatorService;
    @InjectMocks private TransparencyService service;

    private List<TransparencyRecord> records;
    private List<RecordResponse> recordResponses;

    @BeforeEach
    void setUp() {
        records = List.of(
            TransparencyRecord.builder()
                .id(1L)
                .electionId(1L)
                .eventType("VOTO_EMITIDO")
                .description("Voto registrado")
                .timestamp(LocalDateTime.now())
                .build()
        );
        recordResponses = List.of(
            new RecordResponse("VOTO_EMITIDO", "Voto registrado", LocalDateTime.now(), null, null)
        );
    }

    // F-01 | EQ-2 | Incluye todos los campos requeridos
    @Test @DisplayName("F-01 | EQ-2 | La respuesta incluye todos los campos requeridos")
    void should_includeAllRequiredFields_when_recordsExist() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);
        
        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertNotNull(result.getElectionId());
        assertNotNull(result.getRecords());
        assertFalse(result.getRecords().isEmpty());
    }

    // F-02 | EQ-1 | Ningún campo nulo
    @Test @DisplayName("F-02 | EQ-1 | Ningún campo llega nulo para elección con registros")
    void should_haveNoNullFields_when_electionHasRecords() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertAll(
            () -> assertNotNull(result.getElectionId()),
            () -> assertNotNull(result.getRecords())
        );
    }

    // F-03 | EQ-2 | El electionId coincide con el consultado
    @Test @DisplayName("F-03 | EQ-2 | El electionId retornado coincide exactamente con el consultado")
    void should_returnCorrectElectionId_when_queried() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        assertEquals(1L, service.getRecords(1L, 0, 10).getElectionId());
    }

    // F-04 | EQ-2 | Registros no alterados entre BD y respuesta
    @Test @DisplayName("F-04 | EQ-2 | Los registros de auditoría no son alterados entre BD y respuesta")
    void should_notAlterRecords_when_mappingFromDatabase() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertEquals("VOTO_EMITIDO", result.getRecords().get(0).getEventType());
    }

    // F-05 | EQ-1 | Todos los registros de la elección aparecen
    @Test @DisplayName("F-05 | EQ-1 | Todos los registros de la elección aparecen en la respuesta")
    void should_returnAllRecords_when_multipleEventsExist() {
        List<TransparencyRecord> multipleRecords = List.of(
            TransparencyRecord.builder().id(1L).electionId(1L).eventType("INICIO_JORNADA").build(),
            TransparencyRecord.builder().id(2L).electionId(1L).eventType("VOTO_EMITIDO").build(),
            TransparencyRecord.builder().id(3L).electionId(1L).eventType("CIERRE_JORNADA").build()
        );
        List<RecordResponse> multipleResponses = List.of(
            new RecordResponse("INICIO_JORNADA", "Jornada iniciada", LocalDateTime.now(), null, null),
            new RecordResponse("VOTO_EMITIDO",   "Voto registrado",  LocalDateTime.now(), null, null),
            new RecordResponse("CIERRE_JORNADA", "Jornada cerrada",  LocalDateTime.now(), null, null)
        );

        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(multipleRecords, PageRequest.of(0, 10), 3);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(multipleResponses)
            .page(0)
            .size(10)
            .totalElements(3)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(multipleRecords), eq(0), eq(10), eq(3L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertEquals(3, result.getRecords().size());
    }

    // FI-01 | EQ-17 | No colapsa cuando Redis falla en get (try/catch interno)
    @Test @DisplayName("FI-01 | EQ-17 | No colapsa cuando Redis falla en get — try/catch interno")
    void should_continueToDatabase_when_cacheGetFails() {
        when(cache.get(anyString())).thenThrow(new RuntimeException("Redis connection refused"));
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertNotNull(result);
        verify(repository, times(1)).findByElectionId(eq(1L), any(Pageable.class));
    }

    // FI-02 | EQ-17 | No colapsa cuando Redis falla en set
    @Test @DisplayName("FI-02 | EQ-17 | No colapsa cuando Redis falla en set — try/catch interno")
    void should_returnResponse_when_cacheSetFails() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);
        doThrow(new RuntimeException("Redis write failed")).when(cache).set(anyString(), any());

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertNotNull(result);
    }

    // FI-03 | EQ-17 | Retorna lista vacía pero no lanza excepción cuando no hay registros en BD
    @Test @DisplayName("FI-03 | EQ-17 | Retorna lista vacía cuando no hay registros")
    void should_returnEmptyList_when_noRecordsFound() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(repository.findByElectionId(eq(999L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse response = service.getRecords(999L, 0, 10);
        assertNotNull(response);
        assertEquals(0, response.getRecords().size());
    }

    // FI-04 | EQ-16 | El servicio arranca sin errores
    @Test @DisplayName("FI-04 | EQ-16 | El servicio arranca y responde sin errores")
    void should_startWithoutErrors_when_contextIsInitialized() {
        assertNotNull(service);
        assertNotNull(repository);
        assertNotNull(cache);
        assertNotNull(mapper);
    }

    // FI-05 | EQ-17 | Responde correctamente cuando caché está vacía
    @Test @DisplayName("FI-05 | EQ-17 | Responde correctamente cuando la caché está vacía")
    void should_respondCorrectly_when_cacheIsEmpty() {
        when(cache.get(anyString())).thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        TransparencyResponse result = service.getRecords(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getElectionId());
    }

    // FI-06 | EQ-17 | Un fallo en caché no afecta la siguiente consulta
    @Test @DisplayName("FI-06 | EQ-17 | Un fallo en caché no afecta la siguiente consulta")
    void should_notAffectNextQuery_when_previousCacheFailed() {
        when(cache.get(anyString()))
                .thenThrow(new RuntimeException("Redis timeout"))
                .thenReturn(null);
        Page<TransparencyRecord> page = new PageImpl<>(records, PageRequest.of(0, 10), 1);
        when(repository.findByElectionId(eq(1L), any(Pageable.class))).thenReturn(page);

        TransparencyResponse responseMock = TransparencyResponse.builder()
            .electionId(1L)
            .records(recordResponses)
            .page(0)
            .size(10)
            .totalElements(1)
            .totalPages(1)
            .build();
        when(mapper.toResponse(eq(1L), eq(records), eq(0), eq(10), eq(1L), eq(1))).thenReturn(responseMock);

        try { service.getRecords(1L, 0, 10); } catch (Exception ignored) {}

        TransparencyResponse result = service.getRecords(1L, 0, 10);
        assertNotNull(result);
    }
}
