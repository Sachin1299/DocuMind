package com.sachin.documind.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.database.SaveDocument;
import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.Points;
import com.sachin.documind.dto.QdrantRequest;

@Service
public class DocumentService {
	
	private final DocumentReader documentReaderService;
	private final SaveDocument repository;
	
	public DocumentService(DocumentReader documentReaderService, SaveDocument repository) {
		super();
		this.documentReaderService = documentReaderService;
		this.repository = repository;
	}



	public boolean saveAndRead(MultipartFile file) {
		
		List<ChunkEmbedding> embeddings = documentReaderService.generateEmbeddingForFile(file);
		repository.save(embeddings);
		
		
		return false;
	}
	
	public QdrantRequest createBody(List<ChunkEmbedding> embeddings) {
		QdrantRequest result = new QdrantRequest();
		List<Points> points = new ArrayList();
		
		for(ChunkEmbedding chunkEmbedding:embeddings) {
			
		}
		
		
		
		
		return result;
		
		
	}

}
