package com.sachin.documind.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${com.sachin.URL}")
	private String AIAPI;
	
	@Value("${com.sachin.Token}")
	private String AItoken;
	
	@Value("${db.qdrant.url}")
	private String QdrantURL;

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean("AiRequest")
	public WebClient webclient() {
		return WebClient.builder()
				.baseUrl(AIAPI)
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("Authorization", AItoken)
				.build();
	}
	
	@Bean("QdrantRequest")
	public WebClient webclientforqdrant() {
		return WebClient.builder()
				.baseUrl(QdrantURL)
				.defaultHeader("Content-Type", "application/json")
				.build();
	}
	

}
