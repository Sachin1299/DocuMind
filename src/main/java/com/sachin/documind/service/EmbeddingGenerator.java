package com.sachin.documind.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.utility.AiUtility;

@Service
public class EmbeddingGenerator {

	private final AiUtility aiUtility;

	public EmbeddingGenerator(AiUtility aiUtility) {
		super();
		this.aiUtility = aiUtility;
	}

//	public List<Double> embeddingGenertorForFileText(String myText) {
//		List<String> chunks = new ArrayList<String>();
//		int start = 0;
//		int end = 0;
//		while (start < myText.length()) {
//			end = Math.min(start + CHUNCK__SIZE, myText.length());
//			chunks.add(myText.substring(start, end));
//			start += (CHUNCK__SIZE - overlap);
//		}
//		try {
//			return aiUtility.generateEmbeddingUsingAi(chunks);
//		} catch (Exception e) {
//			System.out.println("exception in generate embedding method");
//		}
//
//		return null;
//	}

	public List<ChunkEmbedding> embeddingGenertorForFileText(List<String> chunks, List<ChunkEmbedding> chunkEmbeddings,
			int batchSize) {

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
		ChunkEmbedding chunkEmbedding = new ChunkEmbedding();
		List<Double> embeddings = aiUtility.generateEmbeddings(userQuery);
		chunkEmbedding.setEmbedding(embeddings);
		chunkEmbedding.setText(userQuery);
		return chunkEmbedding;
		
	}

}
