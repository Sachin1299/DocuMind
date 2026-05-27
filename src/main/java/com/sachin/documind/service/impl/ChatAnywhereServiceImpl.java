package com.sachin.documind.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachin.documind.dto.LlmResponse;
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
	private final ObjectMapper objectMapper;
	private static final Logger logger = LoggerFactory.getLogger(ChatAnywhereServiceImpl.class);

	private static final String SYS_PROMPT = """
			You are a grounded document assistant.
			Your job is to answer questions using the provided context.
			
			CORE RULES:
			1. Use ONLY the provided context. If something is missing, say so.
			2. Do NOT use external knowledge.
			3. Return STRICT JSON.
			
			ENHANCED LOGIC:
			- If the user asks about an entity's attribute (e.g. "years of experience") and the context mentions the entity's history, use that history to provide a grounded answer.
			
			CASE 1: Ambiguity -> Use ONLY when context contains related partial info but requires clarification. Suggestions are MANDATORY. {"ambiguity": true, "answer": "", "suggestions": ["clarifying question 1", "clarifying question 2"]}
			CASE 2: Not Found -> Use when context contains NO relevant info about the query. {"ambiguity": false, "answer": "I could not find the answer in the provided document.", "suggestions": []}
			CASE 3: Found -> {"ambiguity": false, "answer": "The grounded answer...", "suggestions": []}
			
			CONTEXT:
			%s
			""";

	public ChatAnywhereServiceImpl(@Qualifier("AiRequest") WebClient webClient, ObjectMapper objectMapper) {
		this.webClient = webClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public List<Double> generateEmbedding(String text) {
		try {
			logger.info("Generating Embeddings for User's question");
			Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", text);
			Map response = webClient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
			logger.info("Embedding generated for user's question");
			return (List<Double>) data.get(0).get("embedding");
		} catch (Exception e) {
			throw new ExternalServiceException("Failed to generate embedding: " + e.getMessage(), e);
		}
	}

	@Override
	public List<List<Double>> generateEmbeddings(List<String> texts) {
		try {
			logger.info("Generating Embeddings for document chunk");
			Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", texts);
			Map response = webClient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
			logger.info("Embeddings generated for document chunk");
			return data.stream().map(d -> (List<Double>) d.get("embedding")).toList();
		} catch (Exception e) {
			throw new ExternalServiceException("Failed to generate batch embeddings: " + e.getMessage(), e);
		}
	}

	@Override
	public LlmResponse generateAnswer(String context, String question) {
		try {
			// Context clearing
			context = context.replaceAll("\\s+", " ");
			context = context.replace(".", ".\n");
			String finalPrompt = SYS_PROMPT.formatted(context);

			List<Map<String, String>> messages = List.of(Map.of("role", "system", "content", finalPrompt),
					Map.of("role", "user", "content", question));

			Map<String, Object> body = Map.of("model", "gpt-3.5-turbo", "messages", messages, "temperature", 0.0);
			Map response = webClient.post().uri("/chat/completions").bodyValue(body).retrieve().bodyToMono(Map.class)
					.block();
			List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
			Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
			String jsonResponse = (String) message.get("content");

			return objectMapper.readValue(jsonResponse, LlmResponse.class);
		} catch (Exception e) {
			throw new ExternalServiceException("Failed to generate Chat completion answer: " + e.getMessage(), e);
		}
	}
}
