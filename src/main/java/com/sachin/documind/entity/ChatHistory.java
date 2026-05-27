package com.sachin.documind.entity;

import jakarta.persistence.Id;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class ChatHistory {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@OneToOne(mappedBy = "chatHistory")
	private User user;
	private String documentId;
	
	@ElementCollection
	private List<String> history;
	public ChatHistory(User user, String documentId, List<String> history) {
		super();
		this.user = user;
		this.documentId = documentId;
		this.history = history;
	}
	public ChatHistory() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public List<String> getHistory() {
		return history;
	}
	public void setHistory(List<String> history) {
		this.history = history;
	}
	
	
	
}
