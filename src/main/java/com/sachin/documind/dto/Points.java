package com.sachin.documind.dto;

import java.util.List;

public class Points {
	
	private int id;
	private List<Double> vector;
	private Payload payload;
	public Points() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Points(int id, List<Double> vector, Payload payload) {
		super();
		this.id = id;
		this.vector = vector;
		this.payload = payload;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Double> getVector() {
		return vector;
	}
	public void setVector(List<Double> vector) {
		this.vector = vector;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	@Override
	public String toString() {
		return "Points [id=" + id + ", vector=" + vector + ", payload=" + payload + "]";
	}
	
	

}
