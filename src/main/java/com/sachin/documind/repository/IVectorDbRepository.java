package com.sachin.documind.repository;

import com.sachin.documind.dto.QdrantRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.QdrantScrollRequest;
import com.sachin.documind.dto.QdrantScrollResponse;
import com.sachin.documind.dto.UserQueryPayload;

public interface IVectorDbRepository {
    String saveData(QdrantRequest requestBody);
    QdrantSearchResponse searchData(UserQueryPayload payload);
    QdrantScrollResponse fetchVectors(QdrantScrollRequest request);
}
