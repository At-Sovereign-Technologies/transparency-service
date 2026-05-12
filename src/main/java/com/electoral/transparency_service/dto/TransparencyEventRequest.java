package com.electoral.transparency_service.dto;

import com.electoral.transparency_service.enums.OriginComponent;
import com.electoral.transparency_service.enums.Severity;
import com.electoral.transparency_service.validation.NoSensitiveData;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO for incoming audit events from microservices.
 * Implements Zero-Identity Enforcement pattern to prevent PII leakage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransparencyEventRequest {

    @NotNull(message = "timestamp is required")
    private Instant timestamp;

    @NotNull(message = "originComponent is required")
    private OriginComponent originComponent;

    @NotBlank(message = "eventType is required and cannot be blank")
    @Size(min = 3, max = 100, message = "eventType must be between 3 and 100 characters")
    private String eventType;

    @NotNull(message = "severity is required")
    private Severity severity;

    @NotNull(message = "details map is required")
    @NoSensitiveData
    private Map<String, Object> details;
}
