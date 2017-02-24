package com.net.test;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.net.dao.ExportDataToCSVDAO;
import com.net.dao.LoadCSVToDB;
import com.net.util.DataGenerationUtil;
import com.net.utils.LogUtils;

public class TestJdbc extends AbstractContextTest {
	
	private static Logger logger = LogUtils.getLogger();
	
	@Autowired
	@Qualifier("namedJdbcTemplate")
	protected NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("exportDataToCSVDAOImpl")
	protected ExportDataToCSVDAO exportDataToCSVDAOImpl;
	
	@Autowired
	@Qualifier("loadCSVToDBImpl")
	protected LoadCSVToDB loadCSVToDBImpl;
	
	@BeforeClass
	public static void setUpOnce() {
		dbDataGenerationUtil = new DataGenerationUtil(
				"/com/net/dao/USER_DTL.csv");
	}
	
	@Test
	public void testHello(){
		logger.info("Hello,World");
	}
	
	@Test
	public void testJdbc(){
		String retrieveSql="select * from user_dtl";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(retrieveSql);
	}
	
	@Test
	public void testExportDataToCSV(){
		String retrieveSql="select * from user_dtl";
		exportDataToCSVDAOImpl.exportDataToCSV(retrieveSql, "USER_DTL", "USER_DTL", "USER_DTL.csv");
	}
	
	@Test
	public void testClearData(){
		exportDataToCSVDAOImpl.clearData("USER_DTL.csv");
	}
	
	@Test
	public void testLoadCSVDataToDB(){
		loadCSVToDBImpl.exceuteStatementIDSQLS("USER_DTL.csv", "USER_DTL");
	}
	
	@Test
	public void testPath(){
		String path = "com/net/dao/";
		ClassLoader loader = this.getClass().getClassLoader();
		if (null == loader) {
			loader = ClassLoader.getSystemClassLoader();
		}
		URL url = loader.getResource(path);
		logger.info("1"+url.getPath());
		//1/C:/Users/lenovo/git/springmvc-demo/target/test-classes/com/net/dao/
		
		String urlPath = loader.getResource("").getPath();
		logger.info("2"+urlPath);
		//2/C:/Users/lenovo/git/springmvc-demo/target/test-classes/
		
		url = loader.getResource("/");
		logger.info("3"+url);
		//3null
		
		path = "/com/net/dao/";
		url = loader.getResource(path);
		logger.info("4"+url);
		//4null
		
		urlPath = System.getProperty("user.dir");
		logger.info("5"+urlPath);
		//5C:\Users\lenovo\git\springmvc-demo
		
		urlPath = System.getProperty("line.separator");
		logger.info("5"+urlPath+"5");
		//5
		//5
		
	}
	

}
