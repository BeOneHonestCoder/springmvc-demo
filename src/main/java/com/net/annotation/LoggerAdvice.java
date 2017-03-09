package com.net.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.net.util.Utils;

@Aspect
@Component
public class LoggerAdvice {
	
	@Before(value = "execution(* *(..)) && @annotation(log)", argNames = "joinPoint, log")
	public void before(JoinPoint joinPoint, LogIt log) {
		Class<? extends Object> clazz = Utils.getJoinPointClass(joinPoint);
		Logger logger = Logger.getLogger(clazz);
		if (Utils.isNotLoggable(logger, log.value())) {
			return;
		}
		String methodName = Utils.getJoinPointMethod(joinPoint);

		Object[] args = joinPoint.getArgs();
		if ((args == null) || (args.length == 0) || (!log.arguments())) {
			logger.log(log.value().getLogLevel(), methodName + " START");
		} else {
			logger.log(log.value().getLogLevel(), methodName + " START - Arguments:" + format(args));
		}
	}

	@AfterReturning(value = "execution(* *(..)) && @annotation(log)", returning = "returnValue", argNames = "joinPoint, log, returnValue")
	public void afterReturning(JoinPoint joinPoint, LogIt log, Object returnValue) {
		Class<? extends Object> clazz = Utils.getJoinPointClass(joinPoint);
		Logger logger = Logger.getLogger(clazz);
		if (Utils.isNotLoggable(logger, log.value())) {
			return;
		}
		String methodName = Utils.getJoinPointMethod(joinPoint);
		if (!log.returnValue()) {
			logger.log(log.value().getLogLevel(), methodName + " END");
			return;
		}
		Signature signature = joinPoint.getSignature();
		if ((signature instanceof MethodSignature)) {
			MethodSignature methodSignature = (MethodSignature) signature;
			Class<?> returnType = methodSignature.getReturnType();
			if (returnType.getSimpleName().equalsIgnoreCase("void")) {
				logger.log(log.value().getLogLevel(), methodName + " END");
				return;
			}
		}
		logger.log(log.value().getLogLevel(),
				methodName + " END returns:" + (returnValue == null ? "null" : returnValue.toString()));
	}

	@AfterThrowing(value = "execution(* *(..)) && @annotation(com.net.annotation.LogIt)", throwing = "throwable", argNames = "joinPoint,throwable")
	public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {
		Class<? extends Object> clazz = Utils.getJoinPointClass(joinPoint);
		String methodName = Utils.getJoinPointMethod(joinPoint);

		Logger logger = Logger.getLogger(clazz);
		logger.error(methodName + " END", throwable);
	}

	private String format(Object[] args) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		for (int i = 0; i < args.length; i++) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			if (args[i] != null) {
				sb.append(args[i].toString());
			} else {
				sb.append("null");
			}
		}
		return sb.toString();
	}
}
