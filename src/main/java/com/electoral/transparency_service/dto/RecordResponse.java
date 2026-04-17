package com.electoral.transparency_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecordResponse {
    private String eventType;
    private String description;
    private LocalDateTime timestamp;
}