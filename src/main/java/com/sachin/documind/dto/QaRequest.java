package com.sachin.documind.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record QaRequest(
    String documentText,
    @NotBlank String question,
    List<String> documentIds
) {}
