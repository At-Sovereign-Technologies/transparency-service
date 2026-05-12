package com.electoral.transparency_service.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful event creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedResponse {
    private Long eventId;
    private Instant timestamp;
    private String message;
}
