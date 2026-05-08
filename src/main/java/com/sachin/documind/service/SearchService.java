package com.sachin.documind.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.UserQueryPayload;
import com.sachin.documind.utility.QdrantAPI;

@Service
public class SearchService {

	
	private final EmbeddingGenerator embeddingGenerator;
	private final QdrantAPI qdrantApi;
	
	
	public SearchService(EmbeddingGenerator embeddingGenerator, QdrantAPI qdrantApi) {
		super();
		this.embeddingGenerator = embeddingGenerator;
		this.qdrantApi = qdrantApi;
	}


	public QdrantSearchResponse search(String userQuery) {
		ChunkEmbedding embeddings = embeddingGenerator.embeddingGenertorForText(userQuery);
		UserQueryPayload payload = new UserQueryPayload(embeddings.getEmbedding(),3);
		return qdrantApi.getData(payload);
	}
	
}
