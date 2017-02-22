package com.net.dao;

public interface ExportDataToCSVDAO {
	
	public void exportDataToCSV(String sql, String stateMentId, String tableName, String fileName);
	
	public void clearData(String fileName);

}
