package com.electoral.transparency_service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransparencyResponse {
    private Long electionId;
    private List<RecordResponse> records;
}