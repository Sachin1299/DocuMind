package com.sachin.documind.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.service.DocumentService;
import com.sachin.documind.service.SearchService;

@RestController
public class DocumentController {

	private final DocumentService documentService;
	private final SearchService searchService;
	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

	public DocumentController(DocumentService documentService, SearchService searchService) {
		super();
		this.documentService = documentService;
		this.searchService = searchService;
	}
	
	@PostMapping("upload/document")
	public ResponseEntity<String> documentReader(@RequestParam("file") MultipartFile file) {
	    try {
	    	logger.info("Request Received on /upload/document");
	        return ResponseEntity.ok(documentService.saveAndRead(file));
	    } catch (Exception e) {
	    	logger.error("Error in Controller of /upload/document. Error: ", e.getMessage());
	        return ResponseEntity.badRequest().body("failed");
	    }
	}
	
	@PostMapping("/search/string")
	private ResponseEntity<QdrantSearchResponse> StringReader(@RequestParam String myText){
		 
		try {
			logger.info("Request Received on /search/string");
			 return ResponseEntity.ok(searchService.search(myText));
		}catch(Exception e) {
			logger.error("Error in Controller of /search/string. Error: ", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
		
		
		
	}
}
