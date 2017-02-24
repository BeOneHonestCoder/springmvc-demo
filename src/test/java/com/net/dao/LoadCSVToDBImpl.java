package com.net.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("loadCSVToDBImpl")
public class LoadCSVToDBImpl implements LoadCSVToDB {
	
	@Autowired
	private transient JdbcTemplate jdbcTemplate;
	
	private transient Map<String, List<String>> statementList;
	private transient boolean bVariableNameInNewRecord = false;
	private transient boolean bNewRecordInStatement = false;
	
	private static final String STATEMENT_ID = "STATEMENT ID";
	private static final String TABLE_NAME = "TABLE NAME";

	private int size = 0;

	public void exceuteStatementIDSQLS(String csvFileName, String statementID) {
		setStatementList(csvFileName);

		final List<String> aStatement = this.statementList.get(statementID);

		final Object[] objects = aStatement.toArray();

		if (objects.length > 0){
			jdbcTemplate.batchUpdate(Arrays.copyOf(objects, objects.length, String[].class));
		}
		
	}
	
	private void setStatementList(final String csvFileName) {

		if (this.statementList == null) {
			statementList = new LinkedHashMap<String, List<String>>();
		} else {
			this.statementList.clear();
		}
		createSQLStatements(csvFileName);
	}

	/**
	 * create SQL statements by using csv file
	 * 
	 * @param fileName
	 * @return
	 */
	private void createSQLStatements(final String fileName) {
		final BufferedReader bufRdr = getBufferedReaderFormLocal(fileName);
		try {
			String line = null;
			List<String> currStatement = null;
			String currTableNames = "";
			List<String> currVariableNames = new LinkedList<String>();
			List<String> currValues = new LinkedList<String>();
			while ((line = bufRdr.readLine()) != null) {
				// keep a space for empty value
				String value = " ";
				final String tmp0 = line.replace(',', ' ');
				if (tmp0.trim().length() > 0) {
					final StringTokenizer st = new StringTokenizer(line, ",");
					while (st.hasMoreTokens()) {
						value = st.nextToken();

						if (value.trim().equalsIgnoreCase(STATEMENT_ID)) {
							bNewRecordInStatement = false;
							currStatement = setStatement(st.nextToken());
							break;
						}
						if (value.trim().equalsIgnoreCase(TABLE_NAME)) {
							currTableNames = st.nextToken();
							bVariableNameInNewRecord = true;
							break;
						}
						if (bVariableNameInNewRecord) {
							currVariableNames = buildVariList(line);
							break;
						}
						if (bNewRecordInStatement) {
							currValues = biuldOneRecordValues(line);
							currStatement.add(createAddSqlStatement(
									currTableNames, currVariableNames,
									currValues));
							break;
						}
					}
				}
			}
			bufRdr.close();

		} catch (IOException e) {
		}
	}

	private List<String> buildVariList(final String line) {

		int col = 0;
		size = countVariables(line);
		final List<String> columns = new LinkedList<String>();
		final StringTokenizer st = new StringTokenizer(line, ",");

		while (st.hasMoreElements()) {
			String value = st.nextToken();
			col++;
			if (value == null || value.trim().length() == 0) {
				if (col == size) {
					// last column has space(s)
					size--;
					bVariableNameInNewRecord = false;
				} else {
				}
				break;
			}
			value = value.toUpperCase();
			columns.add(value);
			if (col == size) {
				bVariableNameInNewRecord = false;
			}
		}
		// next row will be record data
		bNewRecordInStatement = true;

		return columns;
	}

	private List<String> biuldOneRecordValues(final String line) {

		final List<String> columnValues = new LinkedList<String>();

		final String tmp = checkLine(line);
		final StringTokenizer st = new StringTokenizer(tmp, ",");
		String value = " ";

		for (int i = 0; i < size; i++) {
			if (st.hasMoreTokens()) {
				value = st.nextToken();
			}
			columnValues.add(value);
		}
		return columnValues;
	}

	/**
	 * create SQL statements by using csv file
	 * 
	 * @param fileName
	 * @return
	 */
	private String createAddSqlStatement(final String tbNm,
			final List<String> names, final List<String> values) {
		final StringBuilder tmpNames = new StringBuilder();
		final StringBuilder tmpValues = new StringBuilder();
		final int size = names.size();
		for (int i = 0; i < size; i++) {
			String value = values.get(i);
			final String name = names.get(i);
			// ignore empty value
			if (value.trim().length() > 0) {
				value = value.toUpperCase();
				if (tmpNames.length() > 0) {
					tmpNames.append(",");
					tmpNames.append(name);

					tmpValues.append(",");
					tmpValues.append(value);
				} else {
					tmpNames.append(name);
					tmpValues.append(value);
				}
			}
		}

		String sqlStr = null;
		if (tmpNames.length() > 0 && tmpNames.length() > 0) {
			// do not create command without data
			sqlStr = "INSERT INTO " + tbNm + "(" + tmpNames + ")VALUES("
					+ tmpValues + ");";
		}
		return sqlStr;
	}

	/**
	 * check line read from csv file the line removed space for empty column
	 * that will cause incorrect wrong reading data problem
	 * 
	 * @param line
	 * @return String
	 */
	private String checkLine(String line) {
		int pos = line.indexOf(',');
		int posNext = pos;
		while (pos >= 0) {
			posNext = line.indexOf(',', pos + 1);
			if (posNext - pos == 1) {
				line = line.replace(",,", ",null,");
				pos = line.indexOf(',', posNext + 1);
			} else {
				pos = posNext;
			}
		}
		return line;
	}

	/**
	 * count number of variables
	 * 
	 * @param line
	 * @return int
	 */
	private int countVariables(String line) {
		int count = 0;
		int pos = line.indexOf(',');
		while (pos >= 0) {
			count++;
			pos = line.indexOf(',', pos + 1);
		}
		if (count >= 1) {
			while (line.charAt(line.length() - 1) == ',') {
				count--;
				line = line.substring(0, line.length() - 1);
			}
			if (line.charAt(line.length() - 1) != ',') {
				count++;
			}
		}
		return count;
	}

	/**
	 * count number of variables
	 * 
	 * @param line
	 * @return int
	 */
	private List<String> setStatement(final String stID) {
		// new statement
		final List<String> statement = new LinkedList<String>();
		this.statementList.put(stID, statement);
		return statement;
	}

	/**
	 * resolve absolute path
	 * 
	 * @param fileName - relative file name with path
	 * @return BufferedReader
	 */
	private BufferedReader getBufferedReaderFormLocal(final String fileName) {
		BufferedReader bufRdr = null;
		final File file = new File(fileName);
		try {
			if (file.exists()){
				bufRdr = new BufferedReader(new FileReader(file));
			}else{
				bufRdr = getBufferedReader(fileName);
			}	
		} catch (Exception th) {
		}

		return bufRdr;
	}

	/**
	 * resolve the relative path
	 * 
	 * @param resourceName - relative file path
	 * @return BufferedReader
	 */
	private BufferedReader getBufferedReader(final String fileName) {
		BufferedReader bufRdr = null;
		InputStream input = null;
		try {
			input = this.getClass().getResourceAsStream(fileName);
		} catch (Exception th) {
		}

		if (input == null) {
		}
		bufRdr = new BufferedReader(new InputStreamReader(input));
		return bufRdr;
	}

}
