package com.net.lucene;

public class ExtractEvent {

	private String extractFilePath;
	private String finalFileName;

	public ExtractEvent(String extractFilePath, String finalFileName) {
		super();
		this.extractFilePath = extractFilePath;
		this.finalFileName = finalFileName;
	}

	public String getExtractFilePath() {
		return extractFilePath;
	}

	public void setExtractFilePath(String extractFilePath) {
		this.extractFilePath = extractFilePath;
	}

	public String getFinalFileName() {
		return finalFileName;
	}

	public void setFinalFileName(String finalFileName) {
		this.finalFileName = finalFileName;
	}

}
