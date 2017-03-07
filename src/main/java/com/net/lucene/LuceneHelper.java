package com.net.lucene;

import org.apache.lucene.search.IndexSearcher;

public interface LuceneHelper {
	
	public void closeIndexSearchers();
	public void commitLucene(final String extractFilePath);
	public void closeIndexWriters();
	public IndexSearcher createIndexSearcher(final String extractFilePath);

}
