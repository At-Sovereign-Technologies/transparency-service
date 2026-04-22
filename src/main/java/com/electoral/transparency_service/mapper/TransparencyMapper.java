package com.electoral.transparency_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.electoral.transparency_service.dto.RecordResponse;
import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;

@Component
public class TransparencyMapper {

    public List<RecordResponse> toRecordResponseList(List<TransparencyRecord> records) {
        return records.stream()
                .map(r -> new RecordResponse(
                        r.getEventType(), 
                        r.getDescription(), 
                        r.getTimestamp()
                ))
                .toList();
    }

    public TransparencyResponse toResponse(Long electionId, List<TransparencyRecord> records) {

        List<RecordResponse> responseList = toRecordResponseList(records);

        return TransparencyResponse.builder()
                .electionId(electionId)
                .records(responseList)
                .build();
    }

    public TransparencyResponse toResponse(
        Long electionId,
        List<TransparencyRecord> records,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    List<RecordResponse> responseList = toRecordResponseList(records);

        return TransparencyResponse.builder()
                .electionId(electionId)
                .records(responseList)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}