package com.net.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface LogIt {
	LogLevel value() default LogLevel.INFO;

	boolean arguments() default true;

	boolean returnValue() default true;
}
