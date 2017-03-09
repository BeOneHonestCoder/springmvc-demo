package com.net.annotation;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.net.util.Utils;

@Aspect
public class TimingAdvice {
	
	private static ThreadLocal<Stack<Long>> stackLocal = new ThreadLocal<Stack<Long>>();

	@Before(value = "execution(* *(..)) && @annotation(timer)", argNames = "joinPoint, timer")
	public void before(JoinPoint joinPoint, Timer timer) {
		addStartTimeToThreadStack();

		Class<? extends Object> clazz = Utils.getJoinPointClass(joinPoint);
		Logger logger = Logger.getLogger(clazz);
		if (Utils.isNotLoggable(logger, timer.log())) {
			return;
		}
		StringBuilder sb = new StringBuilder("Beginning ");
		sb.append(Utils.getJoinPointMethod(joinPoint));
		sb.append(" execution timing logging");
		String result = sb.toString();

		logger.log(timer.log().getLogLevel(), result);
	}

	private void addStartTimeToThreadStack() {
		long start = System.currentTimeMillis();

		Stack<Long> stack = (Stack) stackLocal.get();
		if (stack == null) {
			stack = new Stack();
			stackLocal.set(stack);
		}
		stack.push(Long.valueOf(start));
	}

	@After(value = "execution(* *(..)) && @annotation(timer)", argNames = "joinPoint, timer")
	public void after(JoinPoint joinPoint, Timer timer) {
		long start = ((Long) ((Stack) stackLocal.get()).pop()).longValue();

		Class<? extends Object> clazz = Utils.getJoinPointClass(joinPoint);
		Logger logger = Logger.getLogger(clazz);
		if (Utils.isNotLoggable(logger, timer.log())) {
			return;
		}
		double delta = getMethodElapsedTime(start, timer.value());

		StringBuilder sb = new StringBuilder("Timing: ");
		sb.append(Utils.getJoinPointMethod(joinPoint));
		if (StringUtils.isNotEmpty(timer.msg())) {
			sb.append(" " + timer.msg());
		}
		sb.append(" ");
		sb.append(delta);
		sb.append(timer.value().getValue());
		String result = sb.toString();

		logger.log(timer.log().getLogLevel(), result);
	}

	private double getMethodElapsedTime(long startTime, TimerUnit timerUnits) {
		long end = System.currentTimeMillis() - startTime;
		switch (timerUnits) {
		case MILLISECOND:
			return end;
		case SECOND:
			return end / 1000.0D;
		case MINUTE:
			return end / 60000.0D;
		case HOUR:
			return end / 3600000.0D;
		}
		Logger.getLogger(getClass())
				.warn("Unknown timer level " + timerUnits + " defaulting to " + TimerUnit.SECOND + " level");

		return end / 1000.0D;
	}
}
