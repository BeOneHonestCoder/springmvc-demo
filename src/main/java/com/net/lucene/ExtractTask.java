package com.net.lucene;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.net.dao.ReadDAO;

public class ExtractTask {

	@Value("${fileThreads}")
	private int fileThreads;

	@Value("${textThreads}")
	private int textThreads;
	
	@Value("${pageSize}")
	private int pageSize;

	@Autowired
	@Qualifier("readDAOImpl")
	protected ReadDAO readDAOImpl;

	@Autowired
	@Qualifier("luceneHelperImpl")
	protected LuceneHelper luceneHelperImpl;

	public void process(final ExtractEvent event) {

		ExecutorService fileExecutorService = null;
		ExecutorService textExecutorService = null;

		try {
			// Executor Service for generation of lucene file
			fileExecutorService = Executors.newFixedThreadPool(fileThreads);

			// Executor Service for generation of text file
			textExecutorService = Executors.newFixedThreadPool(textThreads);

			// write extract File For MasterTable
			writeFileForMasterTable(fileExecutorService, event);

			// new method for file generation in multi thread
			generateText(textExecutorService, event, pageSize);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fileExecutorService.shutdownNow();
			textExecutorService.shutdownNow();
		}
	}

	private void writeFileForMasterTable(ExecutorService fileExecutorService, ExtractEvent event) {

		final AtomicInteger counterDown = new AtomicInteger(0);
		final Object notifierObject = new Object();
		final String retrieveSql = "select id,name,birthday,create_ts from user_dtl";

		ResultSet rs = null;
		try {
			rs = readDAOImpl.generatePreparedStatement(retrieveSql).executeQuery();
			while (!rs.isClosed()) {
				List<Map<String, Object>> rows = readDAOImpl.resultSetToMap(rs);
				counterDown.incrementAndGet();
				FileGenerator scheduleExtractFileGenerator = new FileGenerator(rows, counterDown, notifierObject, event,
						luceneHelperImpl);
				fileExecutorService.submit(scheduleExtractFileGenerator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					readDAOImpl.cleanup();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		synchronized (notifierObject) {
			while (counterDown.intValue() != 0) {
				try {
					notifierObject.wait(50); // timeout for every 50 ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			luceneHelperImpl.commitLucene(event.getExtractFilePath());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			luceneHelperImpl.closeIndexWriters();
		}
	}

	private void generateText(ExecutorService textExecutorService, ExtractEvent event, int pageSize) {
		final CountDownLatch counter = new CountDownLatch(textThreads);
		
		// Submit the runnable task for text file generation.
		try {
			textExecutorService.submit(new TXTExtractor(luceneHelperImpl, counter, event, pageSize));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			counter.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			luceneHelperImpl.closeIndexSearchers();
		}

	}

}
