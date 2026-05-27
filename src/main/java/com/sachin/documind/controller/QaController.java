package com.sachin.documind.controller;

import com.sachin.documind.dto.LlmResponse;
import com.sachin.documind.dto.QaRequest;
import com.sachin.documind.dto.response.ApiResponse;
import com.sachin.documind.service.ISearchAndAnswerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qa")
public class QaController {

    private final ISearchAndAnswerService searchAndAnswerService;

    public QaController(ISearchAndAnswerService searchAndAnswerService) {
        this.searchAndAnswerService = searchAndAnswerService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<LlmResponse>> askQuestion(@Valid @RequestBody QaRequest request) {
        LlmResponse answer = searchAndAnswerService.searchAndAnswer(request);
        return ResponseEntity.ok(ApiResponse.success(answer, "Successfully retrieved answer"));
    }
}
