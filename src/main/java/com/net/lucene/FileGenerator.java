package com.net.lucene;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileGenerator implements Runnable {

	private final transient List<Map<String, Object>> rows;
	private final transient AtomicInteger counterDown;
	private final Object notifierObject;
	private final ExtractEvent event;
	private final LuceneHelper luceneHelperImpl;

	public FileGenerator(List<Map<String, Object>> rows, AtomicInteger counterDown, Object notifierObject,
			ExtractEvent event, LuceneHelper luceneHelperImpl) {
		this.rows = rows;
		this.counterDown = counterDown;
		this.notifierObject = notifierObject;
		this.event = event;
		this.luceneHelperImpl = luceneHelperImpl;
	}

	public void run() {
		try {
			for (Map<String, Object> row : rows) {
				final int id = (Integer) row.get("ID");
				final String name = (String) row.get("NAME");
				final Date birthday = (Date) row.get("BIRTHDAY");
				final Timestamp createTs = (Timestamp) row.get("CREATETS");

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			synchronized (notifierObject) {
				counterDown.decrementAndGet();
				notifierObject.notify();
			}
		}
	}

	private void writeExtractFile() {

	}

}
