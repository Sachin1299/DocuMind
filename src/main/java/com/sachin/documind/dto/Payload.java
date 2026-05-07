package com.sachin.documind.dto;

public class Payload {
	
	private String content;
	private String file;
	public Payload() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Payload(String content, String file) {
		super();
		this.content = content;
		this.file = file;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	@Override
	public String toString() {
		return "Payload [content=" + content + ", file=" + file + "]";
	}
	
	

}
 