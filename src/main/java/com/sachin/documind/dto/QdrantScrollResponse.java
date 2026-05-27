package com.sachin.documind.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QdrantScrollResponse(
    ResultData result,
    String status,
    double time
) {
    public record ResultData(
        List<SearchResult> points,
        @JsonProperty("next_page_offset") String nextPageOffset
    ) {}
}
