package com.sachin.documind.dto;

import java.util.List;

public record LlmResponse(
    boolean ambiguity,
    String answer,
    List<String> suggestions
) {}
