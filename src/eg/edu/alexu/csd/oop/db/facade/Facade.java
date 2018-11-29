package eg.edu.alexu.csd.oop.db.facade;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	
	private void dropDatabase(File files) throws IOException {

		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				dropDatabase(file);
			} else {
				if (!file.delete()) {
					throw new IOException();
				}
			}
		}
		if (!files.delete()) {
			throw new IOException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void evaluateQuery() throws SQLException{
		map = parser.parseQuery(query);
		if (map.isEmpty()) {
			setOpeartionSuccess(false);
			throw new java.sql.SQLException();
		}
		String tableName = (String) map.get("tableName");
		String condition = (String) map.get("where");
		HashMap<String, String> colVal = (HashMap<String, String>) map.get("colMap");
		String operationName = (String) map.get("operation");
		if (operationName.equalsIgnoreCase("create database")) {			
			String path = "." + System.getProperty("file.separator") + "Databases"
					+ System.getProperty("file.separator") + map.get("databaseName");
			File f = new File(path);
			
			if(isDropIfExists() && f.exists()) {
				File files = new File(path);
				try {
					dropDatabase(files);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		} else if(operationName.equalsIgnoreCase("drop database")) {
			File files = new File(currentDatabase);
			try {
				dropDatabase(files);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (operationName.equalsIgnoreCase("drop table")) {
			File f = new File(currentDatabase);
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
			for (int i = 0; i < names.size(); i++) {
				if((names.get(i).contains(tableName))){
					File ff = new File(f.getPath() + System.getProperty("file.separator") + names.get(i));
					ff.delete();
				}
			}
		} else {
			Xml xml = new Xml();
			HashMap<String, List<Row>> tables = xml.getTables(currentDatabase);
			if (!tables.containsKey(tableName) && operationName.equalsIgnoreCase("update")) {
				throw new SQLException();
			}
			exp = factory.makeExpression(operationName, tableName, condition, colVal);
			DTD dtd = new DTD();
			ArrayList<String> schema = dtd.read(currentDatabase, tableName);
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
