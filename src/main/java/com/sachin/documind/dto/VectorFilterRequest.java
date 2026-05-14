package com.sachin.documind.dto;

public record VectorFilterRequest(
    String fileName,
    String textContent,
    Integer limit,
    String offset
) {}
