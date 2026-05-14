package com.sachin.documind.service;

import org.springframework.web.multipart.MultipartFile;

public interface IDocumentService {
    String saveAndRead(MultipartFile file);
}
