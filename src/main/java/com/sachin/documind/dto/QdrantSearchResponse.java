package com.sachin.documind.dto;

import java.util.List;

public class QdrantSearchResponse {

    private List<SearchResult> result;
    private String status;
    private double time;
	public List<SearchResult> getResult() {
		return result;
	}
	public void setResult(List<SearchResult> result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public QdrantSearchResponse(List<SearchResult> result, String status, double time) {
		super();
		this.result = result;
		this.status = status;
		this.time = time;
	}

	

}