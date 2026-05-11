package com.sachin.documind.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.database.SaveDocument;
import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.Payload;
import com.sachin.documind.dto.Points;
import com.sachin.documind.dto.QdrantRequest;

@Service
public class DocumentService {

	private final EmbeddingGenerator embeddingGeneratorService;
	private final SaveDocument repository;
	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

	public DocumentService(EmbeddingGenerator embeddingGeneratorService, SaveDocument repository) {
		super();
		this.embeddingGeneratorService = embeddingGeneratorService;
		this.repository = repository;
	}

	public String saveAndRead(MultipartFile file) {
		
		logger.info("Calling generateEmbeddingForFile method for {} file",file.getOriginalFilename());
		List<ChunkEmbedding> embeddings = generateEmbeddingForFile(file);
		logger.info("Embedding succesfully generated for {} file",file.getOriginalFilename());
		
		logger.info("Calling createBody method");
		QdrantRequest requestBody = createBody(embeddings, file);
		logger.info("RequestBody successfully generated");
		
		logger.info("Calling save method to save embedding in db");
		return repository.save(requestBody);
	}


	public QdrantRequest createBody(List<ChunkEmbedding> embeddings, MultipartFile file) {

		logger.info("Inside the createBody() method");
		QdrantRequest result = new QdrantRequest();

		List<Points> points = new ArrayList<>();

		int id = 1;

		for (ChunkEmbedding chunkEmbedding : embeddings) {

			Payload payload = new Payload();
			payload.setContent(chunkEmbedding.getText());
			payload.setFile(file.getOriginalFilename());

			Points point = new Points();
			point.setId(id++);
			point.setPayload(payload);
			point.setVector(chunkEmbedding.getEmbedding());

			points.add(point);
		}

		result.setPoints(points);

		return result;
	}
	
	public List<ChunkEmbedding> generateEmbeddingForFile(MultipartFile file) {

		logger.info("Inside generateEmbeddingForFile method()");
		String content = "";

		try {
			String fileName = file.getOriginalFilename();

			if (fileName == null) {
				logger.error("File name is missing");
				throw new RuntimeException("File name is missing");
			}

			fileName = fileName.toLowerCase();

			if (fileName.endsWith(".pdf")) {
				logger.info("File identified as a pdf");
				content = extractFromPdf(file);
				logger.info("Text extracted successfully");
			} else if (fileName.endsWith(".docx")) {
				logger.info("File identified as a pdf");
				content = extractFromDocx(file);
				logger.info("Text extracted successfully");
			} else if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
				logger.info("File identified as a text/csv");
				content = new String(file.getBytes());
				logger.info("Text extracted successfully");
			} else {
				logger.error("Unsupported file type for file {}",fileName);
				throw new RuntimeException("Unsupported file type: " + fileName);
			}

			logger.info("cleaning text");
			// ✅ Clean ALL text
			content = content.replaceAll("\\s+", " ").trim();

			if (content.isEmpty()) {
				throw new RuntimeException("File has no readable text (maybe scanned PDF?)");
			}

			// Step 1: Chunking
			List<String> chunks = chunkText(content, 300);
			logger.info("Text divided into chunks successfully");

			// Step 2: Map to DTO
			List<ChunkEmbedding> chunkEmbeddings = chunks.stream().map(ChunkEmbedding::new).toList();

			logger.info("Generating Embedding for chunks");
			// Step 3: generate embedding
			return embeddingGeneratorService.embeddingGenertorForFileText(chunks, chunkEmbeddings, 5);

		} catch (Exception e) {
			throw new RuntimeException("Error processing file", e);
		}
	}
	
	private String extractFromPdf(MultipartFile file) throws IOException {
		logger.info("Inside extractFromPdf method");
		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDFTextStripper stripper = new PDFTextStripper();
			return stripper.getText(document);
		}
	}

	private String extractFromDocx(MultipartFile file) throws IOException {
		logger.info("Inside extractFromDocx method");
		try (XWPFDocument document = new XWPFDocument(file.getInputStream());
				XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
			return extractor.getText();
		}
	}
	
	
	private List<String> chunkText(String text, int chunkSize) {
		logger.info("Inside chunkText method");
		List<String> chunks = new ArrayList<>();

		// ✅ Dynamic overlap (10–20% with bounds)
		int overlap = Math.max(30, Math.min(chunkSize / 5, 100));
		int step = chunkSize - overlap;

		for (int i = 0; i < text.length(); i += step) {
			int end = Math.min(i + chunkSize, text.length());
			chunks.add(text.substring(i, end));

			if (end == text.length())
				break;
		}

		return chunks;
	}


	

}
