package com.net.lucene;

import java.util.Map;

import org.apache.lucene.search.IndexSearcher;

public interface LuceneHelper {
	
	public void closeIndexSearchers();
	public void commitLucene(String extractFilePath);
	public void closeIndexWriters();
	public IndexSearcher createIndexSearcher(String extractFilePath);	
	public void writeIndexFile(Map<String, Object> row, String extractFilePath);

}
