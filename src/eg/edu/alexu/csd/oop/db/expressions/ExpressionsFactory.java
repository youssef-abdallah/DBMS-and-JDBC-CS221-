package eg.edu.alexu.csd.oop.db.expressions;

import java.util.HashMap;

public class ExpressionsFactory {
	private static ExpressionsFactory uniqueInstance = new ExpressionsFactory();
	public static ExpressionsFactory getInstance() {
		return uniqueInstance;
	}
	private ExpressionsFactory() {
		
	}
	
	public Expression makeExpression(String operationName, 
			String tableName, String condition, HashMap<String, String> colVal) {
		if (condition != null) {
			condition = condition.replaceAll(" ", "");
		}
		Expression exp = null;
		if(operationName.equalsIgnoreCase("select")) {
			String columns = null;
			if (colVal == null) {
				columns = "*";
			} else {
				columns = colVal.keySet().toString();
			}
			if(condition == null) {
				exp = new Select(columns, new From(tableName));
			}else {
				exp = new Select(columns, new From(tableName, new Where(condition, "select")));
			}
		} else if(operationName.equalsIgnoreCase("insert")) {
			exp = new Insert(tableName, new Values(colVal));
		} else if(operationName.equalsIgnoreCase("update")) {
			if (condition == null) {
				exp = new Update(tableName, new Set(colVal));
			} else {
				exp = new Update(tableName, new Set(colVal, new Where(condition, "update")));
			}
		} else if(operationName.equalsIgnoreCase("delete")) {
			if (condition == null) {
				exp = new Delete(tableName);
			} else {
				exp = new Delete(tableName, new Where(condition, "delete"));
			}
		}
		
		return exp;
	}
}

