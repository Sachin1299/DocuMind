package com.sachin.documind.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${com.sachin.URL}")
	private String API;
	
	@Value("${com.sachin.Token}")
	private String token;
	
	@Bean
	public WebClient webclient() {
		return WebClient.builder()
				.baseUrl(API)
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("Authorization", token)
				.build();
	}

}
