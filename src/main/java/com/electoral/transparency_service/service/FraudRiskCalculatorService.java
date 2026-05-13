package com.electoral.transparency_service.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.electoral.transparency_service.entity.TransparencyRecord;
import com.electoral.transparency_service.enums.Severity;
import com.electoral.transparency_service.repository.TransparencyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudRiskCalculatorService {

    public static final String CURRENT_ALGORITHM_VERSION = "v1.0-class-project";

    private static final Logger log = LoggerFactory.getLogger(FraudRiskCalculatorService.class);

    private final TransparencyRepository repository;
    private final ObjectMapper objectMapper;

    public void calculateRisk(TransparencyRecord record) {
        int riskScore = 0;

        riskScore += calculateSeverityPoints(record.getSeverity());
        riskScore += calculateContextPoints(record.getDetails());
        riskScore += calculateFrequencyPoints(record);

        record.setRiskScore(riskScore);
        record.setAlgorithmVersion(CURRENT_ALGORITHM_VERSION);
    }

    private int calculateSeverityPoints(Severity severity) {
        if (severity == null) {
            return 0;
        }

        return switch (severity) {
            case CRITICAL -> 50;
            case MEDIUM -> 20;
            case INFO -> 0;
            default -> 0;
        };
    }

    private int calculateContextPoints(String detailsJson) {
        if (detailsJson == null || detailsJson.isBlank()) {
            return 0;
        }

        try {
            Map<String, Object> details = objectMapper.readValue(detailsJson, new TypeReference<Map<String, Object>>() {});
            Object channel = details.get("channel");
            if (channel != null && "REMOTE".equalsIgnoreCase(channel.toString())) {
                return 10;
            }
        } catch (Exception exception) {
            log.debug("Unable to evaluate risk context from details JSON: {}", exception.getMessage());
        }

        return 0;
    }

    private int calculateFrequencyPoints(TransparencyRecord record) {
        if (record.getProvider() == null || record.getTimestamp() == null) {
            return 0;
        }

        LocalDateTime windowStart = record.getTimestamp().minus(1, ChronoUnit.HOURS);
        int recentEvents = repository.countByOriginComponentAndTimestampAfter(
                record.getProvider().name(),
                windowStart
        );

        int totalEventsIncludingCurrent = recentEvents + 1;
        return totalEventsIncludingCurrent > 5 ? 30 : 0;
    }
}