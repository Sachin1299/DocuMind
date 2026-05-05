package com.sachin.documind.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.sachin.documind.dto.ChunkEmbedding;

@Component
public class AiUtility {

	private final RestTemplate restTemplate = new RestTemplate();

	private WebClient webclient;

	public AiUtility(WebClient webclient) {
		this.webclient = webclient;
	}

	public List<Double> generateEmbeddingUsingAi(List<String> chunks) {

		try {

			Map<String, Object> body = new HashMap<>();
			body.put("model", "text-embedding-3-small");
			body.put("input", chunks);

			Map response = webclient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();

			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

			return (List<Double>) data.get(0).get("embedding");
		} catch (Exception e) {
			System.out.println("Exception is ai method");
			System.out.println(e.getMessage());
			return null;
		}

	}

	
	public List<List<Double>> generateEmbeddings(List<String> chunks) {

	    Map<String, Object> body = Map.of(
	            "model", "text-embedding-3-small",
	            "input", chunks
	    );

	    Map response = webclient.post()
	            .uri("/embeddings")
	            .bodyValue(body)
	            .retrieve()
	            .bodyToMono(Map.class)
	            .block();

	    List<Map<String, Object>> data =
	            (List<Map<String, Object>>) response.get("data");

	    return data.stream()
	            .map(d -> (List<Double>) d.get("embedding"))
	            .toList();
	}
}
