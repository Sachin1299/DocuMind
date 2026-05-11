package com.sachin.documind.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.utility.AiUtility;

@Service
public class EmbeddingGenerator {

	private final AiUtility aiUtility;
	private static final Logger logger = LoggerFactory.getLogger(EmbeddingGenerator.class);

	public EmbeddingGenerator(AiUtility aiUtility) {
		super();
		this.aiUtility = aiUtility;
	}

	public List<ChunkEmbedding> embeddingGenertorForFileText(List<String> chunks, List<ChunkEmbedding> chunkEmbeddings,
			int batchSize) {

		logger.info("Inside embeddingGenertorForFileText method");
		for (int i = 0; i < chunks.size(); i += batchSize) {

			List<String> batch = chunks.subList(i, Math.min(i + batchSize, chunks.size()));

			List<List<Double>> embeddings = aiUtility.generateEmbeddings(batch);

			for (int j = 0; j < embeddings.size(); j++) {
				chunkEmbeddings.get(i + j).setEmbedding(embeddings.get(j));
			}
		}

		return chunkEmbeddings;
	}
	
	public ChunkEmbedding embeddingGenertorForText(String userQuery) {
		logger.info("Inside embeddingGenertorForText method");
		ChunkEmbedding chunkEmbedding = new ChunkEmbedding();
		List<Double> embeddings = aiUtility.generateEmbeddings(userQuery);
		chunkEmbedding.setEmbedding(embeddings);
		chunkEmbedding.setText(userQuery);
		return chunkEmbedding;
		
	}

}
