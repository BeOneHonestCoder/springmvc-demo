package com.net.test;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.net.parse.UserInfo;
import com.net.parse.UserParser;
import com.net.util.LogUtil;

public class TestParse extends AbstractContextTest {
	
	private static Logger logger = LogUtil.getLogger();
	
	@Autowired
	@Qualifier("userParser")
	protected UserParser userParser;
	
	
	@Test
	public void testParse(){
		
		UserInfo userInfo = userParser.parserXml("/com/net/parse/UserInfo.xml");
		logger.info(userInfo);
		
	}

}
