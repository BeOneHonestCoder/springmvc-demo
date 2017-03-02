package com.net.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

public class Utils {
	
	public static Class<? extends Object> getJoinPointClass(JoinPoint joinPoint) {
		return joinPoint.getSignature().getDeclaringType();
	}

	public static String getJoinPointMethod(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		return signature.getDeclaringTypeName() + "." + signature.getName();
	}

	public static boolean isNotLoggable(Logger logger, LogLevel level) {
		return !level.isGreaterOrEqual(logger.getEffectiveLevel());
	}

}
