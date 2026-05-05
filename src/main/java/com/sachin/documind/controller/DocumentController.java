package com.sachin.documind.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.service.DocumentReader;

@RestController("/document")
public class DocumentController {

	private final DocumentReader documentReaderService;

	public DocumentController(DocumentReader documentReaderService) {
		super();
		this.documentReaderService = documentReaderService;
	}
	
	@PostMapping("upload/document")
	public ResponseEntity<List<ChunkEmbedding>> documentReader(@RequestParam("file") MultipartFile file) {
	    try {
	        return ResponseEntity.ok(documentReaderService.generateEmbeddingForFile(file));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(Collections.emptyList());
	    }
	}
	
	@PostMapping("/upload/string")
	private ResponseEntity<List<Double>> StringReader(@RequestParam String myText){
		 
		try {
			List<Double> result = (List<Double>)documentReaderService.generateEmbeddingForText(myText);
		    return ResponseEntity.ok(result);
		}catch(Exception e) {
			System.out.println("error in catch block of controller");
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(e.getMessage());
			
			return ResponseEntity.badRequest().build();
		}
		
		
		
	}
}
