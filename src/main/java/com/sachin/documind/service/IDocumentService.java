package com.sachin.documind.service;

import com.sachin.documind.dto.response.UserFileResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IDocumentService {
    String saveAndRead(MultipartFile file);
    List<UserFileResponse> getUserFiles();
}
