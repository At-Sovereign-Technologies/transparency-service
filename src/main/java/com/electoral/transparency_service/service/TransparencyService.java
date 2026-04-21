package com.electoral.transparency_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.electoral.transparency_service.cache.RedisCacheAdapter;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;
import com.electoral.transparency_service.exception.ResourceNotFoundException;
import com.electoral.transparency_service.mapper.TransparencyMapper;
import com.electoral.transparency_service.repository.TransparencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransparencyService {

    private final TransparencyRepository repository;
    private final RedisCacheAdapter cache;
    private final TransparencyMapper mapper;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(TransparencyService.class);

    public TransparencyResponse getRecords(Long electionId) {

        String key = "transparency:" + electionId;

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
            log.info("CACHE HIT - electionId={}", electionId);
            return cached;
        }

        log.info("CACHE MISS - querying DB - electionId={}", electionId);

        List<TransparencyRecord> records = repository.findByElectionId(electionId);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException("No records found");
        }

        TransparencyResponse response = mapper.toResponse(electionId, records);

        cache.set(key, response);
        log.info("CACHE STORE - electionId={}", electionId);

        return response;
    }
}