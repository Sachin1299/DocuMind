package com.sachin.documind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChunkEmbedding(
    @NotBlank String text,
    @NotNull List<Double> embedding
) {}
