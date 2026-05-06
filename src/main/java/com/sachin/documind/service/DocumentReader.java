package com.sachin.documind.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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

	    String content = "";

	    try {
	        String fileName = file.getOriginalFilename();

	        if (fileName == null) {
	            throw new RuntimeException("File name is missing");
	        }

	        fileName = fileName.toLowerCase();

	        if (fileName.endsWith(".pdf")) {
	            content = extractFromPdf(file);
	        } 
	        else if (fileName.endsWith(".docx")) {
	            content = extractFromDocx(file);
	        } 
	        else if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
	            content = new String(file.getBytes());
	        } 
	        else {
	            throw new RuntimeException("Unsupported file type: " + fileName);
	        }

	        // ✅ Clean ALL text
	        content = content.replaceAll("\\s+", " ").trim();

	        if (content.isEmpty()) {
	            throw new RuntimeException("File has no readable text (maybe scanned PDF?)");
	        }

	        // Step 1: Chunking
	        List<String> chunks = chunkText(content, 300);

	        // Step 2: Map to DTO
	        List<ChunkEmbedding> chunkEmbeddings = chunks.stream()
	                .map(ChunkEmbedding::new)
	                .toList();

	        // Step 3: Batch processing
	        return processInBatches(chunks, chunkEmbeddings, 5);

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
   
   private List<String> chunkText(String text, int chunkSize) {
	    List<String> chunks = new ArrayList<>();

	    // ✅ Dynamic overlap (10–20% with bounds)
	    int overlap = Math.max(30, Math.min(chunkSize / 5, 100));
	    int step = chunkSize - overlap;

	    for (int i = 0; i < text.length(); i += step) {
	        int end = Math.min(i + chunkSize, text.length());
	        chunks.add(text.substring(i, end));

	        if (end == text.length()) break;
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
   
   private String extractFromPdf(MultipartFile file) throws IOException {
	    try (PDDocument document = PDDocument.load(file.getInputStream())) {
	        PDFTextStripper stripper = new PDFTextStripper();
	        return stripper.getText(document);
	    }
	}
   
   private String extractFromDocx(MultipartFile file) throws IOException {
	    try (XWPFDocument document = new XWPFDocument(file.getInputStream());
	         XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
	        return extractor.getText();
	    }
	}
   
}
