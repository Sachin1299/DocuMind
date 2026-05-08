package com.sachin.documind.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AiUtility {

	private WebClient webclient;

	public AiUtility(@Qualifier("AiRequest") WebClient webclient) {
		this.webclient = webclient;
	}

	public List<Double> generateEmbeddings(String chunk) {

		try {

			Map<String, Object> body = new HashMap<>();
			body.put("model", "text-embedding-3-small");
			body.put("input", chunk);

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

		Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", chunks);
		try {
			Map response = webclient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();

			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

			return data.stream().map(d -> (List<Double>) d.get("embedding")).toList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
