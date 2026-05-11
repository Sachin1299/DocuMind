package com.sachin.documind.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AiUtility {

	private WebClient webclient;
	private static final Logger logger = LoggerFactory.getLogger(AiUtility.class);

	public AiUtility(@Qualifier("AiRequest") WebClient webclient) {
		this.webclient = webclient;
	}

	public List<Double> generateEmbeddings(String chunk) {
		logger.info("inside generateEmbedding method for single chunk");

		try {

			Map<String, Object> body = new HashMap<>();
			body.put("model", "text-embedding-3-small");
			body.put("input", chunk);
			logger.info("response body created successfully");
			logger.info("calling ai to create embedding");
			Map response = webclient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
			logger.info("successfully recieved response from ai");
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

			return (List<Double>) data.get(0).get("embedding");
		} catch (Exception e) {
			logger.error("exception in generateEmbeddings method in AiUtility");
			logger.error("Exception:  {}",e.getMessage());
			return null;
		}

	}

	public List<List<Double>> generateEmbeddings(List<String> chunks) {
		logger.info("inside generateEmbedding method for multiple chunk");
		Map<String, Object> body = Map.of("model", "text-embedding-3-small", "input", chunks);
		logger.info("response body created successfully");

		try {
			logger.info("calling ai to create embedding");
			Map response = webclient.post().uri("/embeddings").bodyValue(body).retrieve().bodyToMono(Map.class).block();
			logger.info("successfully recieved response from ai");
			List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

			return data.stream().map(d -> (List<Double>) d.get("embedding")).toList();
		} catch (Exception e) {
			logger.error("exception in generateEmbeddings method in AiUtility");
			logger.error("Exception:  {}",e.getMessage());
			return null;
		}

	}
}
