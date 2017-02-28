package com.net.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import com.net.util.LogUtil;

public class DataGenerationUtil {

	private static Logger logger = LogUtil.getLogger();

	private Map<String, ArrayList<String>> statementList;

	private static final String STATEMENT_ID = "STATEMENT ID";

	private static final String TABLE_NAME = "TABLE NAME";

	public static final String RUNDATE = "RUNDATE";

	private static final String DATE_FORMAT_YYYYMMDD_HYPHEN = "yyyy-MM-dd";

	private static final String DATE_FORMAT_DDMMMYY = "ddMMMyy";

	public DataGenerationUtil(String csvFileName) {
		super();
		setStatementList(csvFileName);
	}

	/**
	 * set statement list
	 * 
	 * @param csvFileName
	 * @return
	 */
	public void setStatementList(String csvFileName) {
		if (this.statementList == null) {
			// create a new one
			statementList = new HashMap<String, ArrayList<String>>();
		} else {
			// clear previous statements
			this.statementList.clear();
		}
		// create sql statements
		createSQLStatements(csvFileName);
	}

	/**
	 * Executes all Insert sqls one by one - used by data loader
	 * SampleTestDataLoader
	 * 
	 * @param jdbcTemplate
	 */
	public void exceuteStatements(JdbcTemplate jdbcTemplate) {
		// catch a sql statement
		if (statementList != null) {
			Set<String> keys = statementList.keySet();
			Iterator<String> keySetIt = keys.iterator();
			int count = 0;
			while (keySetIt.hasNext()) {
				count++;
				String statementID = keySetIt.next();
				logger.info("\n\n(" + count + ")" + " statementID= " + statementID + ":");
				ArrayList<String> aStatement = this.statementList.get(statementID);
				for (Iterator<String> it = aStatement.iterator(); it.hasNext();) {
					String insertSQL = it.next();
					try {
						jdbcTemplate.execute(insertSQL);

					} catch (RuntimeException e) {
						String tmp = e.toString();
						if (tmp.indexOf("DB2 SQL error: SQLCODE: -803") >= 0) {
							logger.info(
									"   ---Sample data have been loaded previously for " + insertSQL + "\n      " + e);
						}
					}
				}
			}
		} else {
			logger.info("No sql statement created");
		}
	}

	/**
	 * Executes a group of Insert sql(s) for a statementID - used by JUnit test
	 * 
	 * @param statementID
	 * @param jdbcTemplate
	 */
	public void exceuteStatement(String statementID, JdbcTemplate jdbcTemplate) {
		cleanTestData(statementID, jdbcTemplate);
		ArrayList<String> aStatement = this.statementList.get(statementID);
		for (Iterator<String> it = aStatement.iterator(); it.hasNext();) {
			String insertSQL = it.next();
			logger.info(insertSQL);
			try {
				jdbcTemplate.execute(insertSQL);
			} catch (RuntimeException e) {
				logger.error("exceuteStatement() failed when executing " + insertSQL, e);
			}
		}
	}

	/**
	 * Executes a group of Insert sql(s) for a statementID - used by JUnit test
	 * 
	 * @param statementID
	 * @param jdbcTemplate
	 */
	public void exceuteStatementWithoutClean(String statementID, JdbcTemplate jdbcTemplate) {
		ArrayList<String> aStatement = this.statementList.get(statementID);
		if (CollectionUtils.isEmpty(aStatement)) {
			return;
		}
		for (Iterator<String> it = aStatement.iterator(); it.hasNext();) {
			String insertSQL = it.next();

			logger.info(insertSQL);
			try {
				jdbcTemplate.execute(insertSQL);
			} catch (RuntimeException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * clean test data to avoid duplication
	 * 
	 * @param JdbcTemplate
	 * @param String
	 * @return
	 */
	public void cleanTestData(String statementID, JdbcTemplate jdbcTemplate) {
		ArrayList<String> aStatement = this.statementList.get(statementID);
		if (aStatement == null)
			return;
		for (Iterator<String> it = aStatement.iterator(); it.hasNext();) {
			String insertSQL = it.next();
			cleanATestRecode(jdbcTemplate, insertSQL);
		}
		logger.info("cleaned " + statementID);
	}

	/**
	 * create SQL statements by using csv file
	 * 
	 * @param fileName
	 * @return
	 */
	private void createSQLStatements(String fileName) {
		BufferedReader bufRdr = getBufferedReader(fileName);
		try {
			String line = null;
			boolean bVariableNameInNewRecord = false;
			boolean bNewRecordInStatement = false;
			ArrayList<String> currStatement = null;
			String currStatementsID = "";
			String currTableNames = "";
			ArrayList<String> currVariableNames = new ArrayList<String>();
			ArrayList<String> currValues = new ArrayList<String>();
			int size = 0;
			// read each line of cvs file
			//STATEMENT ID,USER_DTL,,
			//TABLE NAME,USER_DTL,,
			//id,name,birthday,createts
			//2,'lisi','1989-01-21','2017-02-22 20:24:06.0'
			try {
				while ((line = bufRdr.readLine()) != null) {
					// keep a space for empty value
					String value = " ";
					// pass empty line
					// Excel create empty line like
					// ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
					String tmp0 = line.replace(',', ' ');
					if (tmp0.trim().length() > 0) {
						StringTokenizer st = new StringTokenizer(line, ",");
						int col = 0;

						while (st.hasMoreTokens()) {
							value = st.nextToken();
							if (value.trim().equalsIgnoreCase(STATEMENT_ID)) {
								bNewRecordInStatement = false;
								// set statement ID
								value = st.nextToken();
								currStatement = setStatement(value);
								currStatementsID = value;
								size = 0;
								currVariableNames.clear();
								break;
							} else if (value.trim().equalsIgnoreCase(TABLE_NAME)) {
								// set table name
								value = st.nextToken();
								// next row will be variable names in a record
								bVariableNameInNewRecord = true;
								currTableNames = value;
								break;
							} else if (bVariableNameInNewRecord) {
								if (col == 0) {
									size = countVariables(line);
								}
								col++;
								if (value == null || value.trim().length() == 0) {
									if (col == size) {
										// last column has space(s)
										size--;
										bVariableNameInNewRecord = false;
									} else {
										// Found empty name
										System.out.println("Found empty name - stop.");
									}
									break;
								}
								value = value.toUpperCase();
								currVariableNames.add(value);
								if (col == size) {
									bVariableNameInNewRecord = false;
								}
							} else {
								// next row will be record data
								bNewRecordInStatement = true;
								currValues.clear();
								break;
							}
						}
						// create a new sql command
						if (bNewRecordInStatement) {
							// the line removed space for empty column that will
							// cause incorrect reading data problem
							String tmp = checkLine(line);
							st = new StringTokenizer(tmp, ",");
							// set record
							for (int i = 0; i < size; i++) {
								value = " ";
								if (st.hasMoreTokens()) {
									value = st.nextToken();
								}

								if (bVariableNameInNewRecord) {
									currVariableNames.add(value);
									if (i == size - 1) {
										// next row will be record data
										bVariableNameInNewRecord = false;
									}
								} else {
									bNewRecordInStatement = true;
									currValues.add(value);
								}
							}
							// create one in current st
							String aSqlStr = createOneSqlInStatement(currStatementsID, currTableNames,
									currVariableNames, currValues);
							if (aSqlStr != null && aSqlStr.length() > 0) {
								currStatement.add(aSqlStr);
							}
						}
					}
				}
			} catch (IOException e) {
				logger.info(e.getMessage());
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}

	/**
	 * create a SQL statement
	 * 
	 * @param stID
	 * @param currTableNames
	 * @param currVariableNames
	 * @param currValues
	 * @return String
	 */
	private String createOneSqlInStatement(String stID, String currTableNames, List<String> currVariableNames,
			List<String> currValues) {
		String aSql = "";
		// if (stID.indexOf("CREATE") >= 0) {
		aSql = createAddSqlStatement(stID, currTableNames, currVariableNames, currValues);
		// }
		return aSql;
	}

	/**
	 * create SQL statements by using csv file
	 * 
	 * @param fileName
	 * @return
	 */
	private String createAddSqlStatement(String stID, String tbNm, List<String> names, List<String> values) {
		String tmpNames = "";
		String tmpValues = "";
		int size = names.size();
		for (int i = 0; i < size; i++) {
			String value = values.get(i);
			String name = names.get(i);
			// ignore empty value
			if (value.trim().length() > 0) {
				value = value.toUpperCase();
				if (value.indexOf(RUNDATE) != -1) {
					value = calculateRunDateForDB(value);
				}
				if (tmpNames.length() > 0) {
					tmpNames += "," + name;
					tmpValues += "," + value;
				} else {
					tmpNames += name;
					tmpValues += value;
				}
			}
		}
		String sqlStr = null;
		if (tmpNames.length() > 0 && tmpNames.length() > 0) {
			// do not create command without data
			sqlStr = "INSERT INTO " + tbNm + "(" + tmpNames + ")VALUES(" + tmpValues + ");";
		}
		return sqlStr;
	}

	/**
	 * This method calculates the RUNDATE based on the Current Date + No. Days
	 * passed in argument. The format of this input string is "RUNDATE+<number
	 * of days>" where run date is current date.
	 * 
	 * @param value
	 * @return DB2 Date format Sring (yyyy-mm-dd)
	 */
	public static String calculateRunDate(String value, String inputFormat) {
		String retValue = "";
		int numberOfDays = 0;
		try {
			if (value.indexOf(RUNDATE) != -1) {
				String tmpValue = value.substring(value.indexOf(RUNDATE) + 7, value.length()).trim();
				if (!"".equals(tmpValue)) {
					if (tmpValue.indexOf("+") != -1) {
						numberOfDays = new Integer(
								tmpValue.substring(tmpValue.indexOf("+") + 1, tmpValue.length()).trim());
					} else if (tmpValue.indexOf("-") != -1) {
						numberOfDays = -new Integer(
								tmpValue.substring(tmpValue.indexOf("-") + 1, tmpValue.length()).trim());
					}
				}
			}
			// Get the current date
			// Date currDate = new Date();
			// if (numberOfDays != 0) {
			// // Add No. of days to the current date.
			// currDate.addDays(numberOfDays);
			// }
			// retValue = currDate.format(inputFormat);
		} catch (Exception ex) {
		}
		return retValue.toString();
	}

	private static String calculateRunDateForDB(String value) {
		return "'" + calculateRunDate(value, DATE_FORMAT_YYYYMMDD_HYPHEN) + "'";
	}

	public static String calculateRunDateForVO(int value) {
		return calculateRunDate(DataGenerationUtil.RUNDATE + "+" + value, DATE_FORMAT_DDMMMYY);
	}

	/**
	 * check line read from csv file the line removed space for empty column
	 * that will cause incorrect wrong reading data problem
	 * 
	 * @param line
	 * @return String
	 */
	// the line removed space for empty column that will cause incorrect
	// wrong reading data problem
	private String checkLine(String line) {
		int pos = line.indexOf(',');
		int posNext = pos;
		while (pos >= 0) {
			posNext = line.indexOf(',', pos + 1);
			if (posNext - pos == 1) {
				// reset line "643, 'ATP1CCQ',,08/01/2007"
				// back to "643, 'ATP1CCQ', ,08/01/2007"
				line = line.replace(",,", ", ,");
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
			// remove empty column(s) at end - e.g. line
			// ="FEET173_SG,CREATE_ID,CREATE_TS,DISUSE_DT,,,,,,,,,,,,,"
			while (line.charAt(line.length() - 1) == ',') {
				count--;
				line = line.substring(0, line.length() - 1);
			}
			// count last non-empty column at end - e.g. line
			// ="FEET173_SG,CREATE_ID,CREATE_TS,DISUSE_DT"
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
	private ArrayList<String> setStatement(String stID) {
		// new statement
		ArrayList<String> statement = new ArrayList<String>();
		this.statementList.put(stID, statement);
		return statement;
	}

	/**
	 * clean test data to avoid duplication duplication may stop test process.
	 * For an insert SQL, create related delete SQL with same data except date
	 * and time. Then run the delete before insert process.
	 * 
	 * @param JdbcTemplate
	 * @param String
	 * @return
	 */
	private void cleanATestRecode(JdbcTemplate jdbcTemplate, String sql_i) {
		boolean deleteDataFlag = false;
		String sqlStatement = "delete from ";
		int colNameBeginPos = sql_i.indexOf("INSERT INTO ");
		if (colNameBeginPos >= 0) {
			int colNameEndPos = sql_i.indexOf("(");
			// 12 is size of "INSERT INTO "
			if (colNameEndPos >= 12) {
				String tableName = sql_i.substring(colNameBeginPos + 12, colNameEndPos);
				if (tableName != null && tableName.length() > 0) {
					sqlStatement += tableName + " where ";
					String valueSeparater = "VALUES(";
					String separater1 = "";
					int colValueEndPos = 0;
					int colValueBeginPos = sql_i.indexOf(valueSeparater) + 1;
					while (colNameEndPos >= 0) {
						// get column name
						colNameBeginPos = colNameEndPos + 1;
						colNameEndPos = sql_i.indexOf(",", colNameBeginPos);
						// logger.debug("
						// colNameBeginPos="+colNameBeginPos+",colNameEndPos="+colNameEndPos);
						if (colNameEndPos < 0 || colNameEndPos <= colNameBeginPos) {
							break;
						}
						String name = sql_i.substring(colNameBeginPos, colNameEndPos);
						if (name != null && name.length() > 0) {
							// get column value
							colValueEndPos = sql_i.indexOf(",", colValueBeginPos);
							if ("VALUES(".equalsIgnoreCase(valueSeparater)) {
								colValueBeginPos += 6;
							}
							if (colValueEndPos < 0 || colValueEndPos <= colValueBeginPos) {
								// logger.info("!!!
								// colValueBeginPos="+colValueBeginPos+",colValueEndPos="+colValueEndPos);
								break;
							}
							// 7 is size of "VALUES("
							String value = sql_i.substring(colValueBeginPos, colValueEndPos);
							if (value != null && value.length() > 0) {
								// bypass date created by RUNDATE
								boolean bDate = isDate(value);
								if (bDate || (!bDate && // isDate(value) &&
														// bypass CURRENT date
														// and
														// time value
										!value.equalsIgnoreCase("CURRENT TIMESTAMP")
										&& !value.equalsIgnoreCase("CURRENT DATE"))) {
									sqlStatement += separater1 + name;

									sqlStatement += "=" + value;
									deleteDataFlag = true;
								}
								colValueBeginPos = colValueEndPos + 1;
								separater1 = " and ";
								valueSeparater = ",";
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (deleteDataFlag) {
			logger.debug("run sqlStatement=" + sqlStatement);
			jdbcTemplate.execute(sqlStatement);
		}
	}

	/**
	 * Check a date value with format as '2010-12-29'
	 * 
	 * @param String
	 * @return boolean
	 */
	private boolean isDate(String value) {
		boolean bDate = false;
		String tmp = value.trim().replaceAll("'", "");
		if (tmp.trim().length() == 10) {
			tmp = tmp.replaceAll("-", "");
			if (StringUtils.isNumeric(tmp)) {
				bDate = true;
			}
		}
		return bDate;
	}

	/**
	 * Locate the file under local drive. e.g. fileName ="C:/net/TestData.csv";
	 * 
	 * @param fileName
	 * @return BufferedReader
	 */
	private BufferedReader getBufferedReaderFormLocal(String fileName) {
		BufferedReader bufRdr = null;
		File file = new File(fileName);
		try {
			// If csv file is not exist then throw exception
			if (!file.exists()) {
			}
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (Exception th) {
		}
		return bufRdr;
	}

	/**
	 * String fileName = "/com/net/testdata.csv"
	 * 
	 * @param fileName
	 * @return BufferedReader
	 */
	private BufferedReader getBufferedReader(String fileName) {
		BufferedReader bufRdr = null;
		InputStream input = null;
		try {
			input = this.getClass().getResourceAsStream(fileName);
		} catch (Exception e) {
		}

		// If input stream is null then throw exception
		if (input == null) {
		}
		bufRdr = new BufferedReader(new InputStreamReader(input));
		return bufRdr;
	}

}
