package eg.edu.alexu.csd.oop.jdbc.cs32.views;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.sql.*;

public class ExecuteStatement {

	private Connection connection;
	private ResultSetMetaData metaData;
	private Vector<String> columns;
	
	public ExecuteStatement(Connection Connection){
		this.connection = Connection;
	}
	
	public Vector<Vector<String>> runQuery(String mQuery) throws SQLException{
		Vector<Vector<String>> results = new Vector<Vector<String>>();
		Statement statement = (Statement) this.connection.createStatement();
		ResultSet resultSet = statement.executeQuery(mQuery);
		resultSet.absolute(0);
		metaData = resultSet.getMetaData();
		
		int numColumns = metaData.getColumnCount();
		setColumns(numColumns,metaData);
		int len = 0;
		while(resultSet.next()) {
			len++;
		}
		resultSet.absolute(1);
		for (int c = 0; c < len - 1; c++) {
			Vector<String> mRow = new Vector<String>();
			for(int i = 1; i <= numColumns; i++){
				mRow.add(String.valueOf(resultSet.getObject(i)));
			}
			resultSet.next();
			results.add(mRow);
		}
		return results;
	}
	
	public Vector<String> getColumns() throws SQLException{
		return this.columns;
	}
	
	public int runUpdate(String mQuery) throws SQLException{
		Statement mStatement = this.connection.createStatement();
		return mStatement.executeUpdate(mQuery);
	}
	public void runCreate(String mQuery) throws SQLException{
		Statement mStatement = this.connection.createStatement();
		mStatement.execute(mQuery);
		return;
	}
	public void setColumns(int mNumColumns, ResultSetMetaData mMetaData) throws SQLException{
		columns = new Vector<String>();
		for(int i = 1; i <= mNumColumns; i++){
			columns.add(mMetaData.getColumnName(i));
		}
	}
	
	public void takeQueries(String[] queries) throws SQLException {
		Statement statement = this.connection.createStatement();
		for(String query : queries) {
			statement.addBatch(query);
		}
		statement.executeBatch();
		
	}
}
