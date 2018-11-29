package eg.edu.alexu.csd.oop.db.facade;

import java.sql.SQLException;
import java.util.HashMap;

import eg.edu.alexu.csd.oop.db.expressions.ExpressionsFactory;
import eg.edu.alexu.csd.oop.db.parser.Regex;

public class Facade {
	private Regex parser;
	private String query;
	private String currentDatabase;
	private HashMap<String, Object> map;
	private ExpressionsFactory factory;
	
	public Facade(String query) {
		parser = new Regex();
		this.query = query;
		factory = new ExpressionsFactory();
	}
	
	public void evaluateQuery() throws SQLException {
		map = parser.parseQuery(query);
		if(map.isEmpty()) {
			throw new java.sql.SQLException();
		}
		String operationName = (String) map.get("operation");
		if(operationName.equalsIgnoreCase("create database")) {
			
		}
		
	}
}
