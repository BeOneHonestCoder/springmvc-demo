package com.net.test;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.net.redis.RedisMapCache;
import com.net.utils.LogUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mainConfig/redis.xml" })
public class TestRedisConnect extends AbstractJUnit4SpringContextTests {
	
	private static Logger logger = LogUtils.getLogger();
	
	@Autowired
	@Qualifier("redisMapCache")
	private RedisMapCache redisMapCache;
	
	@Test
	public void testHello(){
		logger.info("Hello,World");
	}

}
