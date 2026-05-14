package com.sachin.documind.controller;

import com.sachin.documind.dto.QdrantScrollResponse;
import com.sachin.documind.dto.VectorFilterRequest;
import com.sachin.documind.dto.response.ApiResponse;
import com.sachin.documind.service.IVectorViewerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vectors")
public class VectorViewerController {

    private final IVectorViewerService vectorViewerService;

    public VectorViewerController(IVectorViewerService vectorViewerService) {
        this.vectorViewerService = vectorViewerService;
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<QdrantScrollResponse>> getFilteredVectors(
            @RequestBody VectorFilterRequest filterRequest) {
        QdrantScrollResponse response = vectorViewerService.viewVectors(filterRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Successfully fetched filtered vectors."));
    }
}
