package com.net.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

@Repository("readDAOImpl")
@Scope("prototype")
public class ReadDAOImpl implements ReadDAO {

	@Autowired
	private transient JdbcTemplate jdbcTemplate;

	private transient PreparedStatement ps;
	private transient Connection connection = null;

	@Value("${recordChunkSize}")
	private int recordChunkSize;

	public PreparedStatement generatePreparedStatement(String sql) {
		try {
			ps = getReadConnection().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}

	private Connection getReadConnection() throws SQLException {
		if (null == connection || connection.isClosed()) {
			connection = jdbcTemplate.getDataSource().getConnection();
		}
		return connection;
	}

	public ResultSet generatePreparedStatement(String sql, Object... args) {
		ResultSet rs = null;
		try {
			ps = getReadConnection().prepareStatement(sql);

			int i = 1;
			if (args != null)
				for (Object arg : args) {
					if (arg instanceof Timestamp) {
						ps.setTimestamp(i++, (java.sql.Timestamp) arg);
					} else if (arg instanceof Date) {
						ps.setDate(i++, (Date) arg);
					} else if (arg instanceof Integer) {
						ps.setInt(i++, (Integer) arg);
					} else if (arg instanceof Long) {
						ps.setLong(i++, (Long) arg);
					} else if (arg instanceof Double) {
						ps.setDouble(i++, (Double) arg);
					} else if (arg instanceof Float) {
						ps.setFloat(i++, (Float) arg);
					} else {
						ps.setString(i++, (String) arg);
					}
				}
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public void cleanup() {
		try {
			if (ps != null) {
				ps.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, Object>> resultSetToMap(ResultSet rs) {
		List<Map<String, Object>> rows = null;
		try {
			final ResultSetMetaData md = rs.getMetaData();
			final int columns = md.getColumnCount();
			int count = 0;
			rows = new ArrayList<Map<String, Object>>(recordChunkSize);
			while (count < recordChunkSize && rs.next()) {
				//HashMap<String, Object> row = new LinkedCaseInsensitiveMap<Object>(columns);
				HashMap<String, Object> row = new HashMap<String, Object>(columns);
				for (int i = 1; i <= columns; ++i) {
					row.put(md.getColumnLabel(i), rs.getObject(i));
				}
				count++;
				rows.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rows;
	}

}
