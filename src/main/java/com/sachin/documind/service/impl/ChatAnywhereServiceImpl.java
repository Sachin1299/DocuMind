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

			Your job is to answer questions ONLY using the provided context.

			CORE RULES:
			1. Do NOT use external knowledge.
			2. Do NOT invent information.
			3. Use ONLY the provided context.
			4. Return STRICT JSON only.

			QUESTION HANDLING LOGIC:

			CASE 1: Ambiguous or Generic Question
			- Condition: The user provides a single keyword or a very broad phrase with no clear intent.
			- Action: {"ambiguity": true, "answer": "", "suggestions": ["Topic A", "Topic B"]}
			- Example: User says "java". You find "Java Syntax" and "Java Streams" in context. Return suggestions.

			CASE 2: Clear Question but Answer Not Found
			- Condition: The question has a clear intent, but the retrieved context does not contain relevant information.
			- Action: {"ambiguity": false, "answer": "I could not find the answer in the provided document.", "suggestions": []}
			- Example: User asks "What is Sachin's age?". Context mentions his skills but not his age.
			- IMPORTANT: Missing information is NOT ambiguity.

			CASE 3: Clear Question with Answer Found
			- Condition: The question is clear and the context contains the answer.
			- Action: {"ambiguity": false, "answer": "Grounded answer here.", "suggestions": []}
			- Example: User asks "What are his skills?". Context says "He knows Java and React."

			RESPONSE FORMAT:
			{
			  "ambiguity": boolean,
			  "answer": string,
			  "suggestions": []
			}

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
