package com.net.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("exportDataToCSVDAOImpl")
public class ExportDataToCSVDAOImpl implements ExportDataToCSVDAO {

	@Autowired
	private transient JdbcTemplate jdbcTemplate;

	private List<Map<String, String>> columns;

	private String baseDir = "/src/test/java/com/net/dao/";
	//C:\Users\lenovo\git\springmvc-demo
	private String userDir = System.getProperty("user.dir");
	
	private String lineSeparator = "\r\n";
	
	private static final String STATEMENT_ID = "STATEMENT ID";

	private static final String TABLE_NAME = "TABLE NAME";

	public ExportDataToCSVDAOImpl() {
		super();
		columns = new ArrayList<Map<String, String>>();
	}

	public void exportDataToCSV(String sql, String stateMentId, String tableName, String fileName) {
		StringBuffer buf = new StringBuffer();
		PrintWriter printer = null;
		printer = getDefaultPrinter(fileName);

		buf.append(STATEMENT_ID + "," + stateMentId + lineSeparator);
		buf.append(TABLE_NAME + "," + tableName + lineSeparator);

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		if (result != null && result.size() > 0) {
			for (Map<String, Object> map : result) {
				for (String key : map.keySet()) {
					Map<String, String> tempMap = new HashMap<String, String>();
					tempMap.put(key, getObjectType(map.get(key)));
					buf.append(key + ",");
					columns.add(tempMap);
				}
				buf.append(lineSeparator);
				break;

			}
		}

		if (result != null && result.size() > 0) {
			for (Map<String, Object> map : result) {
				for (Map<String, String> columnMap : columns) {
					buf.append(gernateString(map, columnMap));
				}
				buf.append(lineSeparator);
			}
			buf.append(lineSeparator);

			try {
				printer.append(buf);
				printer.flush();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (printer != null) {
					printer.close();
				}

			}
		}

	}

	public String gernateString(Map<String, Object> mapData, Map<String, String> columnMap) {
		StringBuffer tempBuf = new StringBuffer();
		String key = (String) columnMap.keySet().toArray()[0];
		String data = "";

		if (mapData.get(key) != null)
			if (columnMap.get(key).equals("Boolean")) {
				Object obj = mapData.get(key);
				boolean bl = (Boolean) obj;
				data = bl ? "Y" : "N";
			} else {
				data = mapData.get(key).toString();
			}

		String appendChar = "'";
		if (columnMap.get(key).equals("Integer")) {
			appendChar = "";
		}

		if (mapData.get(key) != null) {
			tempBuf.append(appendChar + data + appendChar + ",");
		} else {
			tempBuf.append(data + ",");
		}

		return tempBuf.toString();
	}

	public PrintWriter getDefaultPrinter(String fileName) {
		PrintWriter printer = null;
		printer = getWrite(buildCSVPath(fileName));
		return printer;

	}

	public PrintWriter getWrite(File file) {
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new FileOutputStream(file, true));
		} catch (FileNotFoundException e) {
		}
		return printer;
	}

	public File buildCSVPath(String fileName) {	
		File baseFile = new File(userDir+baseDir);
		if (!baseFile.exists()) {
			baseFile.mkdirs();
		}
		File defaultFile = new File(userDir+baseDir + fileName);
		if (!defaultFile.exists()) {
			try {
				defaultFile.createNewFile();
			} catch (IOException e) {
			}
		}
		return defaultFile;
	}

	public String getObjectType(Object obj) {
		String type = "String";
		if (obj instanceof String) {
			type = "String";
		} else if (obj instanceof Integer) {
			type = "Integer";
		} else if (obj instanceof BigDecimal) {
			type = "BigDecimal";
		} else if (obj instanceof Boolean) {
			type = "Boolean";
		}
		return type;
	}

	public void clearData(String fileName) {
		File defaultFile = new File(userDir+baseDir + fileName);
		if (defaultFile.exists()) {
			StringBuffer buf = new StringBuffer();
			PrintWriter printer = null;
			try {
				printer = new PrintWriter(new FileOutputStream(defaultFile, false));
				printer.append(buf);
				printer.flush();
			} catch (Exception e) {
			} finally {
				if (printer != null) {
					printer.close();
				}
			}
		}
	}

}
