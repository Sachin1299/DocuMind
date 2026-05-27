package com.sachin.documind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record QdrantScrollRequest(
    Map<String, Object> filter,
    Integer limit,
    @JsonProperty("with_payload") boolean withPayload,
    @JsonProperty("with_vector") boolean withVector,
    String offset
) {}
