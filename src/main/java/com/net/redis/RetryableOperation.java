package com.net.redis;

import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

import com.net.utils.LogUtils;

public class RetryableOperation<T> {
	
	private static Logger logger = LogUtils.getLogger();
	private Callable<T> callable;
	private Runnable runnable;

	public static <T> RetryableOperation<T> create(Callable<T> callable) {
		return new RetryableOperation().withCallable(callable);
	}

	public static RetryableOperation<?> create(Runnable runnable) {
		return new RetryableOperation().withRunnable(runnable);
	}

	public T retry(int retries, int sleepTime, Set<Class<? extends Exception>> retryFor) throws Exception {
		if ((this.callable == null) && (this.runnable == null)) {
			throw new IllegalStateException("Either runnable or callable must be set");
		}
		for (int i = 0; i < retries; i++) {
			try {
				if (i > 0) {
					Thread.sleep(sleepTime);
				}
				if (this.callable != null) {
					return (T) this.callable.call();
				}
				if (this.runnable != null) {
					if (i > 0) {
						Thread.sleep(sleepTime);
					}
					this.runnable.run();
					return null;
				}
			} catch (Exception e) {
				logger.info("Retry count: " + (i + 1));
				if ((retryFor.isEmpty()) || (retryFor.contains(e.getClass()))) {
					if (i == retries - 1) {
						throw e;
					}
				} else {
					throw e;
				}
			}
		}
		return null;
	}

	private RetryableOperation<T> withCallable(Callable<T> callable) {
		this.callable = callable;
		return this;
	}

	private RetryableOperation<T> withRunnable(Runnable runnable) {
		this.runnable = runnable;
		return this;
	}
}
