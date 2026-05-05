package com.sachin.documind.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.utility.AiUtility;


@Service
public class DocumentReader {

	private final int overlap = 10;
	private final int CHUNCK__SIZE = 100;
	private final AiUtility aiUtility;
	
	
	
   public DocumentReader(AiUtility aiUtility) {
		super();
		this.aiUtility = aiUtility;
	}

   public List<ChunkEmbedding> generateEmbeddingForFile(MultipartFile file) {

       try {
           String content = new String(file.getBytes());

           // Clean text
           content = content.replaceAll("\\s+", " ").trim();

           // Step 1: Chunking
           List<String> chunks = chunkText(content, 300);

           // Step 2: Map to DTO
           List<ChunkEmbedding> chunkEmbeddings = chunks.stream()
                   .map(ChunkEmbedding::new)
                   .toList();

           // Step 3: Process in batches
           return processInBatches(chunks, chunkEmbeddings, 20);

       } catch (Exception e) {
           throw new RuntimeException("Error processing file", e);
       }
   }
   
   
   public List<Double> generateEmbeddingForText(String myText) {
	   List<String> chunks = new ArrayList<String>();
	   int start = 0;
	   int end = 0;
	   while(start<myText.length()) {
		   end = Math.min(start+CHUNCK__SIZE, myText.length());
		   chunks.add(myText.substring(start,end));
		   start+=(CHUNCK__SIZE-overlap);
	   }
	   try {
		   return aiUtility.generateEmbeddingUsingAi(chunks);
	   }
	   catch(Exception e) {
		   System.out.println("exception in generate embedding method");
	   }
	   
	   return null;
   }
   
   public List<ChunkEmbedding> MapText(List<String> chunks){
	   List<ChunkEmbedding> chunkEmbeddings = new ArrayList<ChunkEmbedding>();
	   for(String chunk:chunks) {
		   chunkEmbeddings.add(new ChunkEmbedding(chunk));
	   }
	   return chunkEmbeddings;
	   
   }
   
   private List<String> chunkText(String text, int size) {
	    List<String> chunks = new ArrayList<>();

	    for (int i = 0; i < text.length(); i += size) {
	        chunks.add(text.substring(i, Math.min(i + size, text.length())));
	    }

	    return chunks;
	}
   
   private List<ChunkEmbedding> processInBatches(
	        List<String> chunks,
	        List<ChunkEmbedding> chunkEmbeddings,
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
   
}
