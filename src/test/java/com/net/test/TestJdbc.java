package com.net.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class TestJdbc extends AbstractContextTest {
	
	@Autowired
	@Qualifier("namedJdbcTemplate")
	protected NamedParameterJdbcTemplate namedJdbcTemplate;
	
	
	@Test
	public void testJdbc(){
		System.out.println("hhhh");
		
	}
	

}
