package com.electoral.transparency_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.electoral.transparency_service.dto.RecordResponse;
import com.electoral.transparency_service.entity.TransparencyRecord;

@Component
public class TransparencyMapper {

    public RecordResponse toRecordResponse(TransparencyRecord record) {
        return new RecordResponse(
                record.getEventType(),
                record.getDescription(),
                record.getTimestamp()
        );
    }

    public List<RecordResponse> toRecordResponseList(List<TransparencyRecord> records) {
        return records.stream()
                .map(this::toRecordResponse)
                .toList();
    }
}