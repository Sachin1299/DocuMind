package com.sachin.documind.dto;

import jakarta.validation.constraints.NotBlank;

public record Payload(
    @NotBlank String content,
    @NotBlank String file,
    String fileType,
    Integer chunkIndex,
    Integer totalChunks,
    String createdAt,
    Long userId,
    String documentId
) {}