package com.net.test;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.net.redis.RedisMapCache;
import com.net.util.LogUtil;


public class TestRedisConnect extends AbstractContextTest {
	
	private static Logger logger = LogUtil.getLogger();
	
	@Autowired
	@Qualifier("redisMapCache")
	private RedisMapCache redisMapCache;
	
	@Test
	public void testHello(){
		logger.info("Hello,World");
	}
	
	@Test
	public void testPushRedis(){
		//delete
		redisMapCache.delete("hello", "hello");
		
		//put
		redisMapCache.put("hello", "hello", "hello");
		
		//get
		String str = (String) redisMapCache.get("hello", "hello");
		logger.info(str);
	}

}
