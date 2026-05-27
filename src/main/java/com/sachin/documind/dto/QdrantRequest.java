package com.sachin.documind.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

public record QdrantRequest(
    @NotEmpty @Valid List<Points> points
) {}
