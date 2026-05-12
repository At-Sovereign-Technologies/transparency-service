package com.electoral.transparency_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.electoral.transparency_service.dto.EventCreatedResponse;
import com.electoral.transparency_service.dto.TransparencyEventRequest;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.service.TransparencyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/transparency")
@RequiredArgsConstructor
@Validated
public class TransparencyController {

    private final TransparencyService service;

    @GetMapping
    public TransparencyResponse getTransparency(
            @RequestParam
            @Pattern(regexp = "^[0-9]+$", message = "electionId must be numeric")
            String electionId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getRecords(Long.parseLong(electionId), page, size);
    }

    /**
     * Universal audit event ingestion endpoint.
     * Receives events from microservices and persists them to the immutable ledger.
     * 
     * @param event The audit event with zero-identity enforcement
     * @return 201 Created with event ID and timestamp
     */
    @PostMapping("/events")
    public ResponseEntity<EventCreatedResponse> recordEvent(
            @Valid @RequestBody TransparencyEventRequest event
    ) {
        EventCreatedResponse response = service.saveAuditEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}