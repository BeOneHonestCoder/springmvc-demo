package com.net.lucene;

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
				luceneHelperImpl.writeIndexFile(row, event.getExtractFilePath());
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

}
