package com.sachin.documind.dto.response;

public record UserFileResponse(
    String documentId,
    String fileName,
    String fileType,
    String createdAt
) {}
