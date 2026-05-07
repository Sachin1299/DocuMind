package com.sachin.documind.dto;

import java.util.List;

public class QdrantRequest {
	
	private List<Points>points;

	public QdrantRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QdrantRequest(List<Points> points) {
		super();
		this.points = points;
	}

	public List<Points> getPoints() {
		return points;
	}

	public void setPoints(List<Points> points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "QdrantRequest [points=" + points + "]";
	}
	
	
	

}
