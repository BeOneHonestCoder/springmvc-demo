package com.net.annotation;

import org.apache.log4j.Level;

public enum LogLevel {
	DEBUG(Level.DEBUG), ERROR(Level.ERROR), FATAL(Level.FATAL), INFO(Level.INFO), TRACE(Level.TRACE), WARN(Level.WARN);

	private Level logLevel;

	private LogLevel(Level logLevel) {
		this.logLevel = logLevel;
	}

	public Level getLogLevel() {
		return this.logLevel;
	}

	public boolean isGreaterOrEqual(LogLevel level) {
		return isGreaterOrEqual(level.getLogLevel());
	}

	public boolean isGreaterOrEqual(Level level) {
		return getLogLevel().isGreaterOrEqual(level);
	}

}
