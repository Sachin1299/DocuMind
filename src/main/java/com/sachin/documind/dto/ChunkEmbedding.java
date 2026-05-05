package com.sachin.documind.dto;

import java.util.List;

public class ChunkEmbedding {

	private String text;
	private List<Double>embedding;
	public ChunkEmbedding(String text, List<Double> embedding) {
		super();
		this.text = text;
		this.embedding = embedding;
	}
	
	public ChunkEmbedding(String text) {
		super();
		this.text = text;
	}
	
	public ChunkEmbedding(List<Double> embedding) {
		super();
		this.embedding = embedding;
	}
	
	public ChunkEmbedding() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<Double> getEmbedding() {
		return embedding;
	}
	public void setEmbedding(List<Double> embedding) {
		this.embedding = embedding;
	}
	
	
	
}
