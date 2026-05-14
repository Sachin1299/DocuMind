package com.sachin.documind.service.impl;

import com.sachin.documind.dto.QdrantScrollRequest;
import com.sachin.documind.dto.QdrantScrollResponse;
import com.sachin.documind.dto.VectorFilterRequest;
import com.sachin.documind.repository.IVectorDbRepository;
import com.sachin.documind.service.IVectorViewerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VectorViewerServiceImpl implements IVectorViewerService {

    private final IVectorDbRepository vectorDbRepository;

    public VectorViewerServiceImpl(IVectorDbRepository vectorDbRepository) {
        this.vectorDbRepository = vectorDbRepository;
    }

    @Override
    public QdrantScrollResponse viewVectors(VectorFilterRequest filterRequest) {
        Map<String, Object> filter = null;
        
        if ((filterRequest.fileName() != null && !filterRequest.fileName().isBlank()) ||
            (filterRequest.textContent() != null && !filterRequest.textContent().isBlank())) {
            
            List<Map<String, Object>> mustConditions = new ArrayList<>();
            
            if (filterRequest.fileName() != null && !filterRequest.fileName().isBlank()) {
                mustConditions.add(Map.of(
                    "key", "file",
                    "match", Map.of("value", filterRequest.fileName())
                ));
            }
            if (filterRequest.textContent() != null && !filterRequest.textContent().isBlank()) {
                mustConditions.add(Map.of(
                    "key", "content",
                    "match", Map.of("text", filterRequest.textContent()) 
                ));
            }
            
            filter = Map.of("must", mustConditions);
        }
        
        int limit = filterRequest.limit() != null && filterRequest.limit() > 0 ? filterRequest.limit() : 10;
        
        QdrantScrollRequest scrollRequest = new QdrantScrollRequest(
            filter,
            limit,
            true, 
            true, 
            filterRequest.offset()
        );
        
        return vectorDbRepository.fetchVectors(scrollRequest);
    }
}
