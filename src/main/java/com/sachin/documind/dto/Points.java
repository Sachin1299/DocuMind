package com.sachin.documind.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

public record Points(
    int id,
    @NotNull @Valid Payload payload,
    @NotEmpty List<Double> vector
) {}
