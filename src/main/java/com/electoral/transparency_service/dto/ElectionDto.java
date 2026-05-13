package com.electoral.transparency_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElectionDto {
    private Long id;
    private String name;
}
