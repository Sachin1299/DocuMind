package com.sachin.documind.service.impl;

import com.sachin.documind.dto.*;
import com.sachin.documind.dto.response.UserFileResponse;
import com.sachin.documind.exception.DocumentProcessingException;
import com.sachin.documind.repository.IVectorDbRepository;
import com.sachin.documind.repository.UserRepository;
import com.sachin.documind.service.IDocumentService;
import com.sachin.documind.service.ILlmService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements IDocumentService {
    private final ILlmService llmService;
    private final IVectorDbRepository vectorDbRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    public DocumentServiceImpl(ILlmService llmService, IVectorDbRepository vectorDbRepository, UserRepository userRepository) {
        this.llmService = llmService;
        this.vectorDbRepository = vectorDbRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserFileResponse> getUserFiles() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
                .getId();

        Map<String, Object> filter = Map.of("must", List.of(
                Map.of("key", "userId", "match", Map.of("value", userId))
        ));

        QdrantScrollRequest scrollRequest = new QdrantScrollRequest(
                filter,
                100,
                true,
                false,
                null
        );

        QdrantScrollResponse response = vectorDbRepository.fetchVectors(scrollRequest);

        if (response == null || response.result() == null || response.result().points() == null) {
            return List.of();
        }

        return response.result().points().stream()
                .map(SearchResult::payload)
                .filter(payload -> payload != null && payload.documentId() != null)
                .collect(Collectors.toMap(
                        Payload::documentId,
                        p -> new UserFileResponse(p.documentId(), p.file(), p.fileType(), p.createdAt()),
                        (existing, replacement) -> existing
                ))
                .values().stream()
                .sorted(Comparator.comparing(UserFileResponse::createdAt).reversed())
                .toList();
    }

    @Override
    public String saveAndRead(MultipartFile file) {
        logger.info("Processing file: {}", file.getOriginalFilename());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
                .getId();
        String documentId = UUID.randomUUID().toString();

        List<ChunkEmbedding> embeddings = generateEmbeddingForFile(file);
        QdrantRequest requestBody = createBody(embeddings, file, userId, documentId);
        return vectorDbRepository.saveData(requestBody);
    }

    private QdrantRequest createBody(List<ChunkEmbedding> embeddings, MultipartFile file, Long userId, String documentId) {
        List<Points> points = new ArrayList<>();
        int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        String fileName = file.getOriginalFilename();
        String fileType = "";
        if (fileName != null && fileName.contains(".")) {
            fileType = fileName.substring(fileName.lastIndexOf('.'));
        }
        String createdAt = LocalDateTime.now().toString();
        int chunkIndex = 0;
        int totalChunks = embeddings.size();

        for (ChunkEmbedding chunk : embeddings) {
            Payload payload = new Payload(chunk.text(), fileName, fileType, chunkIndex++, totalChunks, createdAt, userId, documentId);
            Points point = new Points(id++, payload, chunk.embedding());
            points.add(point);
        }
        return new QdrantRequest(points);
    }

    private List<ChunkEmbedding> generateEmbeddingForFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new DocumentProcessingException("File name is missing");
        
        String content;
        try {
            fileName = fileName.toLowerCase();
            if (fileName.endsWith(".pdf")) {
                content = extractFromPdf(file);
            } else if (fileName.endsWith(".docx")) {
                content = extractFromDocx(file);
            } else if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
                content = new String(file.getBytes());
            } else {
                throw new DocumentProcessingException("Unsupported file type: " + fileName);
            }
        } catch (IOException e) {
            throw new DocumentProcessingException("Error extracting text from file", e);
        }

        content = content.replaceAll("\\s+", " ").trim();
        if (content.isEmpty()) {
            throw new DocumentProcessingException("File has no readable text.");
        }

        List<String> chunks = chunkText(content, 300);
        
        List<ChunkEmbedding> chunkEmbeddings = new ArrayList<>();
        int batchSize = 5;
        for (int i = 0; i < chunks.size(); i += batchSize) {
            List<String> batch = chunks.subList(i, Math.min(i + batchSize, chunks.size()));
            List<List<Double>> embeddings = llmService.generateEmbeddings(batch);
            for (int j = 0; j < embeddings.size(); j++) {
                chunkEmbeddings.add(new ChunkEmbedding(batch.get(j), embeddings.get(j)));
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

    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int overlap = Math.max(30, Math.min(chunkSize / 5, 100));
        int step = chunkSize - overlap;
        for (int i = 0; i < text.length(); i += step) {
            int end = Math.min(i + chunkSize, text.length());
            chunks.add(text.substring(i, end));
            if (end == text.length()) break;
        }
        return chunks;
    }
}
