package com.sachin.documind.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sachin.documind.database.SaveDocument;
import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.UserQueryPayload;

@Service
public class SearchService {

	
	private final EmbeddingGenerator embeddingGenerator;
	private final SaveDocument repository;
	private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
	
	
	public SearchService(EmbeddingGenerator embeddingGenerator, SaveDocument repository) {
		super();
		this.embeddingGenerator = embeddingGenerator;
		this.repository = repository;
	}


	public QdrantSearchResponse search(String userQuery) {
		
		logger.info("Generating embedding for the user query");
		ChunkEmbedding embeddings = embeddingGenerator.embeddingGenertorForText(userQuery);
		logger.info("Embedding successfully generated for the user query");
		
		logger.info("Generating payload for the user query");
		UserQueryPayload payload = new UserQueryPayload(embeddings.getEmbedding(),3);
		logger.info("Payload succesfully generated for the user query");
		
		logger.info("calling db to search data");
		return repository.getData(payload);
	}
	
}
