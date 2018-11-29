package eg.edu.alexu.csd.oop.db.facade;

import java.io.File;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eg.edu.alexu.csd.oop.db.cre_del.Create;
import eg.edu.alexu.csd.oop.db.expressions.Context;
import eg.edu.alexu.csd.oop.db.expressions.Expression;
import eg.edu.alexu.csd.oop.db.expressions.ExpressionsFactory;
import eg.edu.alexu.csd.oop.db.expressions.Row;
import eg.edu.alexu.csd.oop.db.files.DTD;
import eg.edu.alexu.csd.oop.db.files.Xml;
import eg.edu.alexu.csd.oop.db.parser.Regex;

public class Facade {
	private Regex parser;
	private String query;
	private String currentDatabase;
	private boolean dropIfExists;
	private HashMap<String, Object> map;
	private Expression exp;
	private ExpressionsFactory factory;
	private Context ctx;
	private Object[][] result;
	private boolean opeartionSuccess;

	private void CreateDirectory(String path) {
		File dir = new File(path);

		// create multiple directories at one time
		boolean successful = dir.mkdirs();
		if (successful) {
			// created the directories successfully
			System.out.println("directories were created successfully");
		} else {
			// something failed trying to create the directories
			System.out.println("failed trying to create the directories");
		}
	}

	public Object[][] getResult() {
		return result;
	}
	
	private Object[][] get2DArray(List<String> array) {
		Object[][] ans;
		if (array.size() != 0) {
			String[] s = array.get(0).split(" ");
			int rows = array.size();
			int cols = s.length;
			ans = new Object[rows][cols];
			for (int i = 0; i < rows; i++) {
				String[] tmp = array.get(i).split(" ");
				for (int j = 0; j < cols; j++) {
					ans[i][j] = tmp[j];
				}
			}
			return ans;
		}
		ans = new Object[0][0];
		return ans;
	}

	public Facade() {
		parser = new Regex();
		factory = new ExpressionsFactory();
		opeartionSuccess = true;
		currentDatabase = null;
	}

	public boolean isOpeartionSuccess() {
		return opeartionSuccess;
	}

	public void setOpeartionSuccess(boolean opeartionSuccess) {
		this.opeartionSuccess = opeartionSuccess;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isDropIfExists() {
		return dropIfExists;
	}

	public void setDropIfExists(boolean dropIfExists) {
		this.dropIfExists = dropIfExists;
	}

	public void evaluateQuery() throws SQLException {
		map = parser.parseQuery(query);
		if (map.isEmpty()) {
			setOpeartionSuccess(false);
			throw new java.sql.SQLException();
		}
		String tableName = (String) map.get("tableName");
		String condition = (String) map.get("where");
		HashMap<String, String> colVal = (HashMap) map.get("colMap");
		String operationName = (String) map.get("operation");
		if (operationName.equalsIgnoreCase("create database")) {
			boolean checkDir = false;
			String path = "." + System.getProperty("file.separator") + "Databases"
					+ System.getProperty("file.separator") + map.get("databaseName");
			try {
				this.CreateDirectory(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentDatabase = path;
		} else if (operationName.equalsIgnoreCase("create table")) {
			if (currentDatabase == null) {
				throw new SQLException();
			}
			DTD dtdFile = new DTD();
			HashMap<String, String> colMap = (HashMap<String, String>) map.get("colMap");
			opeartionSuccess = dtdFile.Write(currentDatabase, (String) map.get("tableName"),
					new ArrayList(Arrays.asList(colMap.keySet().toArray())));
					Xml xml = new Xml();
					xml.Write(currentDatabase, tableName, null, "create");
		} else {
			exp = factory.makeExpression(operationName, tableName, condition, colVal);
			DTD dtd = new DTD();
			String path = "." + System.getProperty("file.separator") + "Databases" + System.getProperty("file.separator")
					+ map.get("databaseName");
			ArrayList<String> schema = dtd.read(path, tableName);
			Xml xml = new Xml();
			HashMap<String, List<Row>> tables = xml.getTables(path);
			ctx = new Context(tables, schema);
			List<String> interpretation = exp.interpret(ctx);
			result = get2DArray(interpretation);
			List<Row> rowList = tables.get(tableName);
			List<String> stringList = new ArrayList<String>();
			for(Row row : rowList) {
				stringList.add(row.toString());
			}
			Object[][] newTable = get2DArray(stringList);
			xml.Write(currentDatabase, tableName, newTable, "");
		}

	}
}
