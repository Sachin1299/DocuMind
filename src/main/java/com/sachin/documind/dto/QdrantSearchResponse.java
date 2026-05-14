package com.sachin.documind.dto;

import java.util.List;

public record QdrantSearchResponse(
    List<SearchResult> result,
    String status,
    double time
) {}