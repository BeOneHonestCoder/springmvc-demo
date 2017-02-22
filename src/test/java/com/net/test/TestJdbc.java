package com.net.test;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.net.dao.ExportDataToCSVDAO;

public class TestJdbc extends AbstractContextTest {
	
	private static Logger logger = Logger.getLogger(TestJdbc.class); 
	
	@Autowired
	@Qualifier("namedJdbcTemplate")
	protected NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("exportDataToCSVDAOImpl")
	protected ExportDataToCSVDAO exportDataToCSVDAOImpl;
	
	
	@Test
	public void testJdbc(){
		String retrieveSql="select * from user_dtl";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(retrieveSql);
		logger.info("Hello,World");
	}
	
	@Test
	public void testExportDataToCSV(){
		String retrieveSql="select * from user_dtl";
		exportDataToCSVDAOImpl.exportDataToCSV(retrieveSql, "USER_DTL", "USER_DTL", "USER_DTL.csv");
	}
	
	@Test
	public void testPath(){
		String path = "/com/net/dao/";
		ClassLoader loader = this.getClass().getClassLoader();
		if (null == loader) {
			loader = ClassLoader.getSystemClassLoader();
		}
		URL url = loader.getResource(path);
		
		String urlPath1 = loader.getResource("").getPath();
		logger.info("1"+urlPath1);
		
		loader.getResource("/");
		logger.info("2"+urlPath1);
		
		urlPath1 = System.getProperty("user.dir");
		logger.info("3"+urlPath1);
		
		urlPath1 = System.getProperty("line.separator");
		logger.info("3"+urlPath1+"3");
	}
	

}
