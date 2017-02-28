package com.net.util;

import org.apache.log4j.Logger;

public class LogUtil extends SecurityManager {

	/**
	 * Singleton instance of this class
	 */
	private static LogUtil singleton = new LogUtil();

	/**
	 * @return name of the class from which LogUtil is invoked.
	 */
	public String getClassName() {
		return getClassContext()[2].getName();
	}

	/**
	 * @return Logger object for the application and the class from which the
	 *         logger is called.
	 */
	public static Logger getLogger() {
		return Logger.getLogger(singleton.getClassName());
	}
}
