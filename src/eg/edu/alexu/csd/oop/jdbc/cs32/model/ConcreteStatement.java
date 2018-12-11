package eg.edu.alexu.csd.oop.jdbc.cs32.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.jdbc.cs32.model.ConcreteConnection;

public class ConcreteStatement implements java.sql.Statement {
	private ArrayList<String> batch = new ArrayList<>();
	private Database dbms;
	private boolean isClosed = false;
	private int counter;
	private int queryTimeout = 0;
	private ConcreteConnection connection;
	private int[] results;
	private List<String> columnsNames;
	private static final Logger log = Logger.getLogger(ConcreteStatement.class);


	public ConcreteStatement(Connection connection) {
		this.connection = (ConcreteConnection) connection;
		dbms = ((ConcreteConnection) connection).getDatabase();
		log.info("Statement Created");
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addBatch(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		batch.add(arg0);
		log.info("added "+arg0+" to the batch");
	}

	@Override
	public void cancel() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		batch.clear();
		log.info("batch cleared");
	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub
		isClosed = true;
		log.info("statement closed");
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		boolean success = true;
		String temp = arg0.toLowerCase();
		if (temp.contains("create") && temp.contains("database")) {
			dbms.createDatabase(connection.getPath() + System.getProperty("file.separator") + arg0.split(" ")[2], true);
		} else if ((temp.contains("create") && temp.contains("table"))
				|| (temp.contains("drop") && temp.contains("table"))
				|| (temp.contains("drop") && temp.contains("database"))) {
			success = dbms.executeStructureQuery(arg0);
		} else if (temp.contains("insert") && temp.contains("into") || temp.contains("update")
				|| temp.contains("delete")) {
			counter = this.executeUpdate(arg0);
		} else if (temp.contains("select")) {
			Object[][] select = dbms.executeQuery(arg0);
			if (select.length == 0) {
				success = false;
			}
		} else {
			log.info("Wrong query , a SQL Exception thrown");
			throw new SQLException();
		}
		if(success) {
			log.info("Query executed successfully");
		}
		else {
			log.info("Failed to execute query");
		}
		return success;

	}

	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		results = new int[batch.size()];
		for (int i = 0; i < batch.size(); i++) {
			counter = 0;
			this.execute(batch.get(i));
			results[i] = counter;
		}
		log.info("Executed all "+batch.size()+" queries in the batch");
		batch.clear();
		return results;
	}

	@Override
	public ResultSet executeQuery(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		Object[][] select = dbms.executeQuery(arg0);
		String tableName = dbms.getTableName();
		columnsNames = dbms.getSchema(tableName);
		List<String> types = dbms.getTypes(tableName);
		HashMap<String, String> Cs = dbms.getSC();
		log.info("Select query executed");
		return new Resultset(select, columnsNames, this, tableName, types, Cs);
	}

	@Override
	public int executeUpdate(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		if (isClosed) {
			log.info("statement already closed, an UnsupportedOperationException is thrown");
			throw new UnsupportedOperationException();
		}
		counter = 0;
		counter = dbms.executeUpdateQuery(arg0);
		log.info("Updated query executed , edited rows = "+ counter);
		return counter;
	}

	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		log.info("Returning Connection");
		return connection;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return queryTimeout;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		this.queryTimeout = arg0;
	}

}
