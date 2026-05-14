package com.sachin.documind.repository.impl;

import com.sachin.documind.dto.QdrantRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.QdrantScrollRequest;
import com.sachin.documind.dto.QdrantScrollResponse;
import com.sachin.documind.dto.UserQueryPayload;
import com.sachin.documind.exception.ExternalServiceException;
import com.sachin.documind.repository.IVectorDbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
public class QdrantRepositoryImpl implements IVectorDbRepository {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(QdrantRepositoryImpl.class);

    public QdrantRepositoryImpl(@Qualifier("QdrantRequest") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String saveData(QdrantRequest requestBody) {
        try {
            String response = webClient.put()
                    .uri("/collections/documents/points")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (response != null) {
                return response;
            } else {
                throw new ExternalServiceException("Empty response from Qdrant API on save.");
            }
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to save data to Qdrant: " + e.getMessage(), e);
        }
    }

    @Override
    public QdrantSearchResponse searchData(UserQueryPayload payload) {
        try {
            return webClient.post()
                    .uri("/collections/documents/points/search")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(QdrantSearchResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to search data in Qdrant: " + e.getMessage(), e);
        }
    }

    @Override
    public QdrantScrollResponse fetchVectors(QdrantScrollRequest request) {
        try {
            return webClient.post()
                    .uri("/collections/documents/points/scroll")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(QdrantScrollResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch/scroll data from Qdrant: " + e.getMessage(), e);
        }
    }
}
