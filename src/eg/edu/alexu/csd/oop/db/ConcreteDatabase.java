package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;

import eg.edu.alexu.csd.oop.db.cs32.facade.Facade;

public class ConcreteDatabase implements Database{
	
	private Facade facade;
	boolean drop = false;
	
	public ConcreteDatabase() {
		facade = new Facade();
	}

	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		String query = "CREATE DATABASE "+ databaseName;
		String path = "." + System.getProperty("file.separator") + "Databases" + System.getProperty("file.separator")
		+ databaseName;
		facade.setDropIfExists(dropIfExists);
		try {
			executeStructureQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		facade.setQuery(query);
		facade.evaluateQuery();
		return facade.isOpeartionSuccess();
	}

	@Override
	public Object[][] executeQuery(String query) throws SQLException {
		facade.setQuery(query);
		facade.evaluateQuery();
		return facade.getResult();
	}

	@Override
	public int executeUpdateQuery(String query) throws SQLException {
		facade.setQuery(query);
		facade.evaluateQuery();
		return facade.getResult().length;
	}

}
