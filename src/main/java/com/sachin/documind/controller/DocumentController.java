package com.sachin.documind.controller;

import com.sachin.documind.dto.response.ApiResponse;
import com.sachin.documind.service.IDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final IDocumentService documentService;

    public DocumentController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        String result = documentService.saveAndRead(file);
        return ResponseEntity.ok(ApiResponse.success(result, "Document uploaded and indexed successfully"));
    }
}
