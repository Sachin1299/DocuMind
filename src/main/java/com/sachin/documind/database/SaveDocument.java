package com.sachin.documind.database;


import org.springframework.stereotype.Repository;

import com.sachin.documind.dto.QdrantRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.UserQueryPayload;
import com.sachin.documind.utility.QdrantAPI;

@Repository
public class SaveDocument {
	
	private final QdrantAPI qdrantAPI;
	public SaveDocument(QdrantAPI qdrantAPI) {
		super();
		this.qdrantAPI = qdrantAPI;
	}



	public String save(QdrantRequest requestBody) {
		return qdrantAPI.saveData(requestBody);
	}



	public QdrantSearchResponse getData(UserQueryPayload payload) {
		return qdrantAPI.getData(payload);
	}

}
