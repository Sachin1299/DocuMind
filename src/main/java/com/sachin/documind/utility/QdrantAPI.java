package com.sachin.documind.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import com.sachin.documind.dto.QdrantRequest;

@Repository
public class QdrantAPI {
	
	@Autowired
	@Qualifier("QdrantRequest")
	private WebClient webclient;
	
	public boolean saveData(QdrantRequest requestBody) {
		
        String response = webclient.put()
                .uri("/collections/documents/points")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response != null;
		
	}
	

}
