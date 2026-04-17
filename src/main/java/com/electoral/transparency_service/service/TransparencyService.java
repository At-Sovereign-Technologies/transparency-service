package com.electoral.transparency_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.electoral.transparency_service.cache.RedisCacheAdapter;
import com.electoral.transparency_service.dto.RecordResponse;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;
import com.electoral.transparency_service.exception.ResourceNotFoundException;
import com.electoral.transparency_service.repository.TransparencyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransparencyService {

    private final TransparencyRepository repository;
    private final RedisCacheAdapter cache;

    private static final Logger log = LoggerFactory.getLogger(TransparencyService.class);

    public TransparencyResponse getRecords(Long electionId) {

        String key = "transparency:" + electionId;

        Object cached = cache.get(key);
        if (cached != null) {
            log.info("CACHE HIT - electionId={}", electionId);
            return (TransparencyResponse) cached;
        }

        log.info("CACHE MISS - querying DB - electionId={}", electionId);

        List<TransparencyRecord> records = repository.findByElectionId(electionId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No records found");
        }

        List<RecordResponse> responseList = records.stream()
                .map(r -> new RecordResponse(
                        r.getEventType(),
                        r.getDescription(),
                        r.getTimestamp()
                ))
                .toList();

        TransparencyResponse response = TransparencyResponse.builder()
                .electionId(electionId)
                .records(responseList)
                .build();

        cache.set(key, response);

        return response;
    }
}