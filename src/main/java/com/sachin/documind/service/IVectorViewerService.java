package com.sachin.documind.service;

import com.sachin.documind.dto.QdrantScrollResponse;
import com.sachin.documind.dto.VectorFilterRequest;

public interface IVectorViewerService {
    QdrantScrollResponse viewVectors(VectorFilterRequest filterRequest);
}
