package com.sachin.documind.dto;

public record JwtAuthResponse(
        String accessToken,
        String tokenType
) {
    public JwtAuthResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
