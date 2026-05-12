package com.electoral.transparency_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.electoral.transparency_service.cache.RedisCacheAdapter;
import com.electoral.transparency_service.dto.EventCreatedResponse;
import com.electoral.transparency_service.dto.TransparencyEventRequest;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;
import com.electoral.transparency_service.exception.ResourceNotFoundException;
import com.electoral.transparency_service.mapper.TransparencyMapper;
import com.electoral.transparency_service.repository.TransparencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransparencyService {

    private final TransparencyRepository repository;
    private final RedisCacheAdapter cache;
    private final TransparencyMapper mapper;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(TransparencyService.class);

    public TransparencyResponse getRecords(Long electionId, int page, int size) {

        String key = "transparency:" + electionId + ":" + page + ":" + size;

        Object cachedObj = cache.get(key);

        TransparencyResponse cached = null;

        if (cachedObj != null) {
            try {
                cached = objectMapper.convertValue(cachedObj, TransparencyResponse.class);
            } catch (Exception e) {
                log.warn("CACHE CONVERSION ERROR - key={} - {}", key, e.getMessage());
            }
        }

        if (cached != null) {
            log.info("CACHE HIT - electionId={} page={}", electionId, page);
            return cached;
        }

        log.info("CACHE MISS - querying DB - electionId={} page={}", electionId, page);

        Pageable pageable = PageRequest.of(page, size);

        Page<TransparencyRecord> recordsPage =
                repository.findByElectionId(electionId, pageable);

        if (recordsPage.isEmpty()) {
            throw new ResourceNotFoundException("No records found");
        }

        TransparencyResponse response = mapper.toResponse(
                electionId,
                recordsPage.getContent(),
                recordsPage.getNumber(),
                recordsPage.getSize(),
                recordsPage.getTotalElements(),
                recordsPage.getTotalPages()
        );

        cache.set(key, response);
        log.info("CACHE STORE - electionId={} page={}", electionId, page);

        return response;
    }

    /**
     * Saves an audit event from a microservice to the immutable ledger.
     * 
     * @param request The audit event request with zero-identity enforcement applied
     * @return EventCreatedResponse with event ID and timestamp
     */
    public EventCreatedResponse saveAuditEvent(TransparencyEventRequest request) {
        try {
            // Convert details map to JSON string for storage
            String detailsJson = objectMapper.writeValueAsString(request.getDetails());

            // Build and persist the audit record
            TransparencyRecord record = TransparencyRecord.builder()
                    .eventType(request.getEventType())
                    .timestamp(LocalDateTime.ofInstant(request.getTimestamp(), java.time.ZoneOffset.UTC))
                    .provider(request.getOriginComponent())
                    .severity(request.getSeverity())
                    .details(detailsJson)
                    .eventTimestamp(request.getTimestamp())
                    .build();

            TransparencyRecord saved = repository.save(record);

            log.info("AUDIT EVENT SAVED - eventId={} originComponent={} eventType={} severity={}",
                    saved.getId(), request.getOriginComponent(), request.getEventType(), request.getSeverity());

            return EventCreatedResponse.builder()
                    .eventId(saved.getId())
                    .timestamp(request.getTimestamp())
                    .message("Audit event successfully recorded in immutable ledger")
                    .build();

        } catch (Exception e) {
            log.error("AUDIT EVENT SAVE ERROR - eventType={} - {}", request.getEventType(), e.getMessage(), e);
            throw new RuntimeException("Failed to save audit event", e);
        }
    }
}