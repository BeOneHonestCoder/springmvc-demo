package com.net.lucene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

/**
 * This class is a callable task for generating split text file for Schedule
 * Split
 * 
 * @author
 */
public class TXTExtractor implements Runnable {

	private transient final LuceneHelper luceneHelper;
	private final transient CountDownLatch counter;
	private final ExtractEvent event;
	private final transient int pageSize;
	private volatile static ConcurrentMap<String, Writer> fileWriterHolder = new ConcurrentHashMap<String, Writer>();

	public TXTExtractor(LuceneHelper luceneHelper, CountDownLatch counter, ExtractEvent event, int pageSize) {
		super();
		this.luceneHelper = luceneHelper;
		this.counter = counter;
		this.event = event;
		this.pageSize = pageSize;
	}

	public void run() {
		IndexSearcher searcher = null;

		try {
			String finalFileName = event.getFinalFileName();
			if (fileWriterHolder.get(finalFileName) == null) {
				Writer fileWriter = buildWriteFile(finalFileName);
				fileWriterHolder.put(finalFileName, fileWriter);
			}

			final File splitFileDirectory = new File(event.getExtractFilePath());

			if (splitFileDirectory.exists()) {

				searcher = luceneHelper.createIndexSearcher(event.getExtractFilePath());
				int page = 0;
				TopDocs result = null;
				ScoreDoc scoreDoc = null;
				while (page == 0 || (scoreDoc != null && (result.scoreDocs.length == pageSize))) {
					// Retrieve Hits using index searcher to use
					// while writing data
					result = retrieveHitsWithPagination(searcher, pageSize, result, scoreDoc);

					ScoreDoc[] hits = result.scoreDocs;

					appendDataWithMultiSearch(hits, searcher);

					if (null != result.scoreDocs && result.scoreDocs.length == pageSize) {
						scoreDoc = result.scoreDocs[pageSize - 1];
					}
					page++;
				}
			}

		} catch (Throwable e) {
			counter.countDown();
			e.printStackTrace();
		} finally {
			clearFileWriter();
			counter.countDown();
		}

	}

	private void appendDataWithMultiSearch(ScoreDoc[] hits, IndexSearcher searcher) {

		for (int i = 0; i < hits.length; i++) {
			final int docId = hits[i].doc;

			try {
				Document doc = searcher.doc(docId);
				Writer fileWriter = fileWriterHolder.get(event.getFinalFileName());
				IndexableField fields[] = doc.getFields("ID");
				if (fields != null) {
					for (IndexableField field : fields) {
						final String data = field.stringValue() + System.getProperty("line.separator");
						fileWriter.write(data);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private TopDocs retrieveHitsWithPagination(IndexSearcher searcher, int pagesize, TopDocs result,
			ScoreDoc scoreDoc) {
		BooleanQuery query = new BooleanQuery();
		final SortField sortField = new SortField("ID", SortField.Type.INT, false);
		final Sort sort = new Sort(sortField);

		try {
			result = searcher.searchAfter(scoreDoc, query, pagesize, sort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private Writer buildWriteFile(String fileFullName) {
		final File writeFile = new File(fileFullName);
		try {
			if (!writeFile.exists()) {
				writeFile.getParentFile().mkdirs();
				writeFile.createNewFile();
			} else {
				if (writeFile.length() > 0) {
					writeFile.delete();
					writeFile.createNewFile();
				}
			}
		} catch (Exception e) {
			writeFile.deleteOnExit();
		}
		Writer fw = null;
		try {
			// 1MB with Encoding Output Writer
			fw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(writeFile.getAbsoluteFile(), true), "ISO8859_1"),
					1024 * 1024 * 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fw;
	}

	private void clearFileWriter() {
		for (Entry<String, Writer> entrySet : fileWriterHolder.entrySet()) {
			Writer writer = entrySet.getValue();
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
