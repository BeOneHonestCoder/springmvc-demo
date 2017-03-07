package com.net.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface ReadDAO {
	
	public PreparedStatement generatePreparedStatement(String sql);
	
	public ResultSet generatePreparedStatement(final String sql, final Object... args);
	
	public void cleanup();
	
	public List<Map<String, Object>> resultSetToMap(ResultSet rs);

}
