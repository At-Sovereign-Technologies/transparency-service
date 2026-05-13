package com.electoral.transparency_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electoral.transparency_service.dto.ElectionDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/elections/dev")
@RequiredArgsConstructor
public class DevElectionsController {

    @GetMapping
    public ResponseEntity<List<ElectionDto>> listDevElections() {
        List<ElectionDto> elections = List.of(
                ElectionDto.builder().id(1L).name("Mock Election 1").build(),
                ElectionDto.builder().id(2L).name("Mock Election 2").build(),
                ElectionDto.builder().id(3L).name("Mock Election 3").build()
        );

        return ResponseEntity.ok(elections);
    }
}
