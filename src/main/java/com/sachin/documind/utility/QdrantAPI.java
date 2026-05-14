package com.sachin.documind.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.sachin.documind.dto.QdrantRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.UserQueryPayload;

@Repository
public class QdrantAPI {
	
	@Autowired
	@Qualifier("QdrantRequest")
	private WebClient webclient;
	private static final Logger logger = LoggerFactory.getLogger(QdrantAPI.class);
	public String saveData(QdrantRequest requestBody) {
		logger.info("Inside saveData");

	        String response = webclient.put()
	                .uri("/collections/documents/points")
	                .bodyValue(requestBody)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();

	        if(response!=null) {
				logger.info("Embedding saved successfully in db");
				return response;
	        }
	        else {
				logger.warn("empty response from db api");
				return null;
	        }

	
	}
	
	public QdrantSearchResponse getData(UserQueryPayload payload) {
		logger.info("Inside getData");
		
			QdrantSearchResponse response = webclient.post()
	                .uri("/collections/documents/points/search")
	                .bodyValue(payload)
	                .retrieve()
	                .bodyToMono(QdrantSearchResponse.class)
	                .block();
			logger.info("searched succesfully in db");
	        return response;
		
	}
	

}
