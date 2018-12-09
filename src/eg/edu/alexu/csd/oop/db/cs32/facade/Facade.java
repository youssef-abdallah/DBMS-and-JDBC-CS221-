package eg.edu.alexu.csd.oop.db.cs32.facade;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import eg.edu.alexu.csd.oop.db.cs32.expressions.Context;
import eg.edu.alexu.csd.oop.db.cs32.expressions.Expression;
import eg.edu.alexu.csd.oop.db.cs32.expressions.ExpressionsFactory;
import eg.edu.alexu.csd.oop.db.cs32.expressions.Row;
import eg.edu.alexu.csd.oop.db.cs32.files.DTD;
import eg.edu.alexu.csd.oop.db.cs32.files.Xml;
import eg.edu.alexu.csd.oop.db.cs32.parser.Regex;

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
	private String tableName ;
	
	public String getTableName() {
		return tableName;
	}

	private void CreateDirectory(String path) {
		File dir = new File(path);

		// create multiple directories at one time
		dir.mkdirs();

	}

	public Object[][] getResult() {
		return result;
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
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
					if (isInteger(tmp[j])) {
						ans[i][j] = Integer.valueOf(tmp[j]);
					} else {
						ans[i][j] = tmp[j];
					}
				}
			}
			return ans;
		}
		ans = new Object[0][0];
		return ans;
	}

	private Object[][] get2DArrayXml(List<String> array) {
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
		parser = Regex.getInstance();
		factory = ExpressionsFactory.getInstance();
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
	public void evaluateQuery() throws SQLException {
		map = parser.parseQuery(query);
		if (map.isEmpty()) {
			setOpeartionSuccess(false);
			throw new java.sql.SQLException();
		}
		tableName = (String) map.get("tableName");
		String condition = (String) map.get("where");
		HashMap<String, String> colVal = (HashMap<String, String>) map.get("colMap");
		String operationName = (String) map.get("operation");
		if (operationName.equalsIgnoreCase("create database")) {
			String path = (String) map.get("databaseName");
			File f = new File(path);

			if (isDropIfExists() && f.exists()) {
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
			ArrayList columnsNames = new ArrayList<>();
			for (int i = 0; i < colMap.size(); i++) {
				columnsNames.add(colMap.get(String.valueOf(i)));
			}
			opeartionSuccess = dtdFile.Write(currentDatabase, (String) map.get("tableName"), columnsNames);
			Xml xml = new Xml();
			xml.Write(currentDatabase, tableName, null, "create");
		} else if (operationName.equalsIgnoreCase("drop database")) {
			if (currentDatabase == null) {
				return;
			}
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
				if ((names.get(i).contains(tableName))) {
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
			for (Row row : rowList) {
				stringList.add(row.toString());
			}
			Object[][] newTable = get2DArrayXml(stringList);
			xml.Write(currentDatabase, tableName, newTable, "");
		}

	}
}
