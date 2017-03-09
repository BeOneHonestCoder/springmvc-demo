package com.net.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Timer {
	TimerUnit value() default TimerUnit.MILLISECOND;

	LogLevel log() default LogLevel.INFO;

	String msg() default "";
}
