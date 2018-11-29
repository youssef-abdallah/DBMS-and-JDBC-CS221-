package eg.edu.alexu.csd.oop.db.facade;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eg.edu.alexu.csd.oop.db.cre_del.Create;
import eg.edu.alexu.csd.oop.db.expressions.Expression;
import eg.edu.alexu.csd.oop.db.expressions.ExpressionsFactory;
import eg.edu.alexu.csd.oop.db.files.DTD;
import eg.edu.alexu.csd.oop.db.parser.Regex;

public class Facade {
	private Regex parser;
	private String query;
	private String currentDatabase;
	private boolean dropIfExists;
	private HashMap<String, Object> map;
	private Expression exp;

	public Facade(String query, boolean drop) {
		parser = new Regex();
		this.query = query;
		dropIfExists = drop;
	}

	public void evaluateQuery() throws SQLException {
		map = parser.parseQuery(query);
		String tableName = (String)map.get("tableName");
		String condition = (String)map.get("where");
		HashMap <String, String> colVal = (HashMap)map.get("colMap");
		if (map.isEmpty()) {
			throw new java.sql.SQLException();
		}
		String operationName = (String) map.get("operation");
		if (operationName.equalsIgnoreCase("create database")) {
			boolean checkDir = false;
			String path = System.getProperty("file.separator") + "Databases" + System.getProperty("file.separator")
					+ map.get("databaseName");
			try {
				Path xpath = Paths.get(path);
				checkDir = Files.exists(xpath);
				if (checkDir) {
				} else {
					Files.createDirectory(xpath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentDatabase = path;
		} else if (operationName.equalsIgnoreCase("create table")) {
			DTD dtdFile = new DTD();
			HashMap<String, String> colMap = (HashMap<String, String>) map.get("colMap");
			dtdFile.Write(currentDatabase, (String) map.get("tableName"),
					new ArrayList(Arrays.asList(colMap.keySet().toArray())));
		} else if (operationName.equalsIgnoreCase("insert")) {
		   exp = new ExpressionsFactory("insert", tableName, condition, colVal);
		}

	}
}
