package com.electoral.transparency_service.entity;

import java.time.Instant;
import java.time.LocalDateTime;

import com.electoral.transparency_service.enums.OriginComponent;
import com.electoral.transparency_service.enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transparency_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransparencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long electionId;

    private String eventType;

    private String description;

    @Column(name = "record_timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private OriginComponent provider;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Lob
    private String details;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "algorithm_version")
    private String algorithmVersion;

    @Column(name = "event_timestamp")
    private Instant eventTimestamp;
}