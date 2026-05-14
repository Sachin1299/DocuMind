package com.sachin.documind.dto;

import java.util.List;

public record SearchResult(
    int id,
    Double score,
    Payload payload,
    List<Double> vector,
    Integer version
) {}
