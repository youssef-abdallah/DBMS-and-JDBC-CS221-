package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import eg.edu.alexu.csd.oop.db.cs32.facade.Facade;
import eg.edu.alexu.csd.oop.db.cs32.files.DTD;
import eg.edu.alexu.csd.oop.db.cs32.files.Xml;

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

	@Override
	public List<String> getSchema(String TableName) {
		DTD d = new DTD();
		return d.read(facade.getPath(), TableName);
	}
	
	@Override
	public List<String> getTypes(String TableName) {
		Xml x = new Xml();
		return x.getTypes(facade.getPath(), TableName);
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return facade.getTableName();
	}

	@Override
	public HashMap<String, String> getSC() throws SQLException {
		return facade.gettype();
	}

}
