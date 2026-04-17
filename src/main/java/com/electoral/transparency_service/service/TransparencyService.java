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
import com.electoral.transparency_service.mapper.TransparencyMapper;
import com.electoral.transparency_service.repository.TransparencyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransparencyService {

    private final TransparencyRepository repository;
    private final RedisCacheAdapter cache;
    private final TransparencyMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(TransparencyService.class);

    public TransparencyResponse getRecords(Long electionId) {

        String key = "transparency:" + electionId;

        TransparencyResponse cached = null;
        try {
            cached = cache.get(key, TransparencyResponse.class);
        } catch (Exception e) {
            log.warn("CACHE ERROR (ignored) - electionId={}", electionId);
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

        List<RecordResponse> responseList = mapper.toRecordResponseList(records);

        TransparencyResponse response = TransparencyResponse.builder()
                .electionId(electionId)
                .records(responseList)
                .build();

        try {
            cache.set(key, response);
        } catch (Exception e) {
            log.warn("CACHE STORE ERROR (ignored) - electionId={}", electionId);
        }

        return response;
    }
}