package com.sachin.documind.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.Map;

public record UserQueryPayload(
    @NotEmpty List<Double> vector,
    @Positive int limit,
    @JsonProperty("with_payload") boolean withPayload,
    Map<String, Object> filter
) {}
