package com.net.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Util class used for manipulate mocked record for non transactional test case
 * 
 * 
 */
public class ApplicationContextUtil {

	private static ApplicationContext ctx;

	public ApplicationContextUtil(String classPath) {
		ctx = new ClassPathXmlApplicationContext(classPath.split(","));
	}

	public <T> T getBean(String paramString, Class<T> paramClass) throws BeansException {
		return ctx.getBean(paramString, paramClass);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getBeanList(String paramString, Class<T> paramClass) throws BeansException {
		List<T> list = ctx.getBean(paramString, List.class);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> Set<T> getBeanSet(String paramString, Class<T> paramClass) throws BeansException {
		Set<T> set = ctx.getBean(paramString, Set.class);
		return set;
	}

	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getBeanMap(String paramString, Class<K> paramClassKey, Class<V> paramClassValue)
			throws BeansException {
		Map<K, V> map = ctx.getBean(paramString, Map.class);
		return map;
	}

	public Object getDesiriedObject(String beanName) {
		return ctx.getBean(beanName);
	}

}
