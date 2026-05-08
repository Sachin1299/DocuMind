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

import com.sachin.documind.database.SaveDocument;
import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.Payload;
import com.sachin.documind.dto.Points;
import com.sachin.documind.dto.QdrantRequest;

@Service
public class DocumentService {

	private final EmbeddingGenerator embeddingGeneratorService;
	private final SaveDocument repository;

	public DocumentService(EmbeddingGenerator embeddingGeneratorService, SaveDocument repository) {
		super();
		this.embeddingGeneratorService = embeddingGeneratorService;
		this.repository = repository;
	}

	public String saveAndRead(MultipartFile file) {

		List<ChunkEmbedding> embeddings = generateEmbeddingForFile(file);
		QdrantRequest requestBody = createBody(embeddings, file);
		return repository.save(requestBody);
	}


	public QdrantRequest createBody(List<ChunkEmbedding> embeddings, MultipartFile file) {

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

		String content = "";

		try {
			String fileName = file.getOriginalFilename();

			if (fileName == null) {
				throw new RuntimeException("File name is missing");
			}

			fileName = fileName.toLowerCase();

			if (fileName.endsWith(".pdf")) {
				content = extractFromPdf(file);
			} else if (fileName.endsWith(".docx")) {
				content = extractFromDocx(file);
			} else if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
				content = new String(file.getBytes());
			} else {
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
			List<ChunkEmbedding> chunkEmbeddings = chunks.stream().map(ChunkEmbedding::new).toList();

			// Step 3: generate embedding
			return embeddingGeneratorService.embeddingGenertorForFileText(chunks, chunkEmbeddings, 5);

		} catch (Exception e) {
			throw new RuntimeException("Error processing file", e);
		}
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
	
	
	private List<String> chunkText(String text, int chunkSize) {
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
