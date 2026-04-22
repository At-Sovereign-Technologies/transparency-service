package com.electoral.transparency_service.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.electoral.transparency_service.dto.TransparencyResponse;
import com.electoral.transparency_service.service.TransparencyService;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transparency")
@RequiredArgsConstructor
@Validated
public class TransparencyController {

    private final TransparencyService service;

    @GetMapping
    public TransparencyResponse getTransparency(
            @RequestParam
            @Pattern(regexp = "^[0-9]+$", message = "electionId must be numeric")
            String electionId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getRecords(Long.parseLong(electionId), page, size);
    }
}