package com.net.lucene;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.ThreadInterruptedException;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import com.net.annotation.LogLevel;
import com.net.annotation.Timer;
import com.net.util.LogUtil;

@Component("luceneHelperImpl")
public class LuceneHelperImpl implements LuceneHelper {

	private static Logger LOGGER = LogUtil.getLogger();

	private volatile static ConcurrentMap<String, Directory> indexDirectoryHolder = new ConcurrentHashMap<String, Directory>();
	private volatile static ConcurrentMap<String, IndexReader> indexReaderHolder = new ConcurrentHashMap<String, IndexReader>();
	private volatile static ConcurrentMap<String, IndexSearcher> indexSearcherHolder = new ConcurrentHashMap<String, IndexSearcher>();
	private volatile static ConcurrentMap<String, IndexWriter> indexWriterHolder = new ConcurrentHashMap<String, IndexWriter>();

	/**
	 * Create index searcher from extractFilePath
	 * 
	 * @param extractFilePath
	 */
	public IndexSearcher createIndexSearcher(final String extractFilePath) {
		File splitFileDirectory = null;
		Directory directory = null;
		IndexReader indexReader = null;
		IndexSearcher searcher = indexSearcherHolder.get(extractFilePath);
		try {
			if (null == searcher) {
				synchronized (this) {
					if (null == searcher) {
						splitFileDirectory = new File(extractFilePath);
						directory = FSDirectory.open(splitFileDirectory);
						indexReader = DirectoryReader.open(directory);
						searcher = new IndexSearcher(indexReader);
						indexDirectoryHolder.putIfAbsent(extractFilePath, directory);
						indexReaderHolder.putIfAbsent(extractFilePath, indexReader);
						indexSearcherHolder.putIfAbsent(extractFilePath, searcher);
					}
				}
			}
		} catch (ThreadInterruptedException te) {
		} catch (Exception e) {
			try {
				if (indexReader != null) {
					indexReader.close();
				}
				if (directory != null) {
					directory.close();
				}
			} catch (IOException ioException) {
			}
		}
		return searcher;
	}

	/**
	 * write lucene index file in extractFilePath
	 * 
	 * @param rs
	 * @param columnList
	 * @param extractFilePath
	 * @param subscriptionProductString
	 * @param recordTypeList
	 */
	@Timer(log = LogLevel.INFO)
	public void writeIndexFile(final Map<String, Object> row, final String extractFilePath) {
		File indexFile;
		Directory index;
		IndexWriter indexWriter = indexWriterHolder.get(extractFilePath);
		try {
			if (null == indexWriter) {
				synchronized (this) {
					indexWriter = indexWriterHolder.get(extractFilePath);
					if (indexWriter == null) {
						indexFile = new File(extractFilePath);
						if (!indexFile.exists()) {
							indexFile.mkdirs();
						} else {
							// Delete the Lucence/text File if failed Job is
							// resumed
							deleteFiles(indexFile);
						}

						index = FSDirectory.open(indexFile);
						Analyzer analyzer = new StandardAnalyzer();
						final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);

						indexWriter = new IndexWriter(index, config);
						if (index.listAll().length != 0) {
							indexWriter.deleteAll();
						}
						indexWriterHolder.putIfAbsent(extractFilePath, indexWriter);
					}
				}

			}
			final Document doc = new Document();

			List<String> sortKeyList = new ArrayList<String>();
			sortKeyList.add("ID");

			for (String columnName : row.keySet()) {

				Object value = row.get(columnName);
				boolean isSortKey = sortKeyList.contains(columnName);

				String toStringValue = null;
				Field field = null;
				FieldType subDataTextFieldType = new FieldType();
				subDataTextFieldType.setIndexed(false);
				subDataTextFieldType.setOmitNorms(true);
				subDataTextFieldType.setStored(true);
				subDataTextFieldType.setTokenized(false);
				if (value != null) {
					if (value instanceof java.sql.Timestamp) {
						value = new Long(((java.sql.Timestamp) value).getTime());
					} else if (value instanceof java.sql.Date) {
						value = new Long(((Date) value).getTime());
					}

					toStringValue = value.toString();
					// for non sort keys, remove index
					if (!isSortKey) {
						field = new Field(columnName, toStringValue, subDataTextFieldType);
					} else {
						field = new StringField(columnName, toStringValue, Store.YES);
					}
				}
				if (field != null) {
					doc.add(field);
				}
			}
			indexWriter.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (indexWriter != null) {
					indexWriter.close();
				}
			} catch (IOException ioException) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * delete file/folder[include subs files ]
	 * 
	 * @param dir
	 */
	private void deleteFiles(final File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					deleteFiles(file);
				} else {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("delete file:" + file.getAbsolutePath());
					}
					file.delete();
				}
			}
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("delete folder:" + dir.getAbsolutePath());
		}
		dir.delete();
	}

	public void closeIndexSearchers() {
		try {
			indexSearcherHolder.clear();
			for (IndexReader indexReader : indexReaderHolder.values()) {
				indexReader.close();
			}
			indexReaderHolder.clear();
			for (Directory directory : indexDirectoryHolder.values()) {
				directory.close();
			}
			indexDirectoryHolder.clear();
		} catch (IOException e) {
		}
	}

	public void closeIndexWriters() {
		try {
			for (IndexWriter indexWriter : indexWriterHolder.values()) {
				indexWriter.close();
			}
			indexWriterHolder.clear();
		} catch (CorruptIndexException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Method to commit indexWriter
	 * 
	 * @param extractFilePath
	 */
	public void commitLucene(final String extractFilePath) {
		final IndexWriter indexWriter = indexWriterHolder.get(extractFilePath);
		if (indexWriter != null) {
			try {
				indexWriter.commit();
			} catch (Exception e) {
				indexWriterHolder.remove(extractFilePath);
				try {
					indexWriter.close();
				} catch (IOException ioException) {
				}
			}
		}

	}

}
