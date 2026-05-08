package com.sachin.documind.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.dto.ChunkEmbedding;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.service.DocumentService;
import com.sachin.documind.service.SearchService;

@RestController("/document")
public class DocumentController {

	private final DocumentService documentService;
	private final SearchService searchService;

	public DocumentController(DocumentService documentService, SearchService searchService) {
		super();
		this.documentService = documentService;
		this.searchService = searchService;
	}
	
	@PostMapping("upload/document")
	public ResponseEntity<String> documentReader(@RequestParam("file") MultipartFile file) {
	    try {
	        return ResponseEntity.ok(documentService.saveAndRead(file));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("failed");
	    }
	}
	
	@PostMapping("/upload/string")
	private ResponseEntity<QdrantSearchResponse> StringReader(@RequestParam String myText){
		 
		try {
			 return ResponseEntity.ok(searchService.search(myText));
		}catch(Exception e) {
			System.out.println("error in catch block of controller");
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(e.getMessage());
			
			return ResponseEntity.badRequest().build();
		}
		
		
		
	}
}
