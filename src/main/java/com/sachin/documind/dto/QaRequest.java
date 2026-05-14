package com.sachin.documind.dto;

import jakarta.validation.constraints.NotBlank;

public record QaRequest(
    String documentText,
    @NotBlank String question
) {}
