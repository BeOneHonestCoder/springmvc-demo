package com.net.utils;

import java.util.logging.Logger;

public class LogUtils extends SecurityManager {

	/**
	 * Singleton instance of this class
	 */
	private static LogUtils singleton = new LogUtils();

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
