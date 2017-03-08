package com.net.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.net.lucene.ExtractEvent;
import com.net.lucene.ExtractTask;

public class TestLucene extends AbstractContextTest {
	
	@Autowired
	@Qualifier("extractTask")
	protected ExtractTask extractTask;
	
	@Test
	public void testProcess(){
		
		ExtractEvent event = new ExtractEvent("D:/logs/user/user_dtl/a", "D:/logs/user/user_dtl/b");
		extractTask.process(event);
		
	}

}
