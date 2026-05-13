package com.electoral.transparency_service.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electoral.transparency_service.dto.TransparencyEventRequest;
import com.electoral.transparency_service.enums.OriginComponent;
import com.electoral.transparency_service.enums.Severity;
import com.electoral.transparency_service.service.TransparencyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transparency/dev")
@RequiredArgsConstructor
public class DevMockController {

    private final TransparencyService transparencyService;

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedMockData() {
        Instant now = Instant.now();

        List<TransparencyEventRequest> mockEvents = List.of(
            createEvent(now, OriginComponent.GATEWAY, Severity.INFO, "NORMAL_EVENT_1", Map.of("channel", "LOCAL", "electionId", 1L)),
            createEvent(now, OriginComponent.GATEWAY, Severity.INFO, "NORMAL_EVENT_2", Map.of("channel", "LOCAL", "electionId", 1L)),
            createEvent(now, OriginComponent.GATEWAY, Severity.INFO, "NORMAL_EVENT_3", Map.of("channel", "LOCAL", "electionId", 1L)),
            createEvent(now, OriginComponent.GATEWAY, Severity.MEDIUM, "REMOTE_EVENT_1", Map.of("channel", "REMOTE", "electionId", 1L)),
            createEvent(now, OriginComponent.GATEWAY, Severity.MEDIUM, "REMOTE_EVENT_2", Map.of("channel", "REMOTE", "electionId", 1L)),
            createEvent(now, OriginComponent.GATEWAY, Severity.CRITICAL, "DUPLICATE_QR_SCAN", Map.of("channel", "LOCAL", "reason", "duplicate_scan", "electionId", 1L))
        );

        mockEvents.forEach(transparencyService::saveAuditEvent);

        for (int ignored = 0; ignored < 6; ignored++) {
            transparencyService.saveAuditEvent(createEvent(
                now,
                    OriginComponent.COMPUTE_ENGINE,
                    Severity.INFO,
                    "FREQUENT_COMPUTE_EVENT",
                    Map.of("channel", "LOCAL", "batch", "compute-engine", "electionId", 1L)
            ));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Mock data processed successfully");
        response.put("eventsCreated", mockEvents.size() + 6);

        return ResponseEntity.ok(response);
    }

    private TransparencyEventRequest createEvent(Instant timestamp,
                                                 OriginComponent originComponent,
                                                 Severity severity,
                                                 String eventType,
                                                 Map<String, Object> details) {
        return TransparencyEventRequest.builder()
                .timestamp(timestamp)
                .originComponent(originComponent)
                .eventType(eventType)
                .severity(severity)
                .details(new LinkedHashMap<>(details))
                .build();
    }
}