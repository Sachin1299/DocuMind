package com.sachin.documind.service.impl;

import com.sachin.documind.exception.ExternalServiceException;
import com.sachin.documind.service.ILlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Service
public class ChatAnywhereServiceImpl implements ILlmService {
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(ChatAnywhereServiceImpl.class);

    public ChatAnywhereServiceImpl(@Qualifier("AiRequest") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Double> generateEmbedding(String text) {
        try {
            Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", text);
            Map response = webClient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            return (List<Double>) data.get(0).get("embedding");
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }

    @Override
    public List<List<Double>> generateEmbeddings(List<String> texts) {
        try {
            Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", texts);
            Map response = webClient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            return data.stream().map(d -> (List<Double>) d.get("embedding")).toList();
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to generate batch embeddings: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAnswer(String context, String question) {
        try {
            List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are an intelligent document assistant. Answer the user's question purely based on the provided context. If the answer is not in the context, say 'I cannot find the answer in the provided document.'\n\nContext:\n" + context),
                Map.of("role", "user", "content", question)
            );
            Map<String, Object> body = Map.of("model", "gpt-3.5-turbo", "messages", messages, "temperature", 0.3);
            Map response = webClient.post().uri("/chat/completions").bodyValue(body).retrieve().bodyToMono(Map.class).block();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to generate Chat completion answer: " + e.getMessage(), e);
        }
    }
}
