package com.sachin.documind.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserQueryPayload {

	private List<Double> vector;
	private int limit;
	
	@JsonProperty("with_payload")
	private Boolean with_payload = true;
	public UserQueryPayload(List<Double> vector, int limit) {
		super();
		this.vector = vector;
		this.limit = limit;
	}
	public UserQueryPayload() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<Double> getVector() {
		return vector;
	}
	public void setVector(List<Double> vector) {
		this.vector = vector;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public Boolean getWith_payload() {
		return with_payload;
	}
	
	
	
}
