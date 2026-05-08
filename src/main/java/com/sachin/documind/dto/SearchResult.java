package com.sachin.documind.dto;

public class SearchResult {

    private int id;
    private double score;
    private Payload payload;
	public SearchResult(int id, double score, Payload payload) {
		super();
		this.id = id;
		this.score = score;
		this.payload = payload;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
    
}
