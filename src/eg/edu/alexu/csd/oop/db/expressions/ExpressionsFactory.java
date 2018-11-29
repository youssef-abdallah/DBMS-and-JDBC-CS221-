package eg.edu.alexu.csd.oop.db.expressions;

import java.util.HashMap;

public class ExpressionsFactory {
	
	public Expression ExpressionFactory(String operationName, 
			String tableName, String condition, HashMap<String, String> colVal) {
		Expression exp = null;
		if(operationName.equalsIgnoreCase("select")) {
			String columns = colVal.keySet().toString();
			if(condition == null) {
				exp = new Select(columns, new From(tableName));
			}else {
				exp = new Select(columns, new From(tableName, new Where(condition, "select")));
			}
		} else if(operationName.equalsIgnoreCase("insert")) {
			exp = new Insert(tableName, new Values(colVal));
		} else if(operationName.equalsIgnoreCase("update")) {
			exp = new Update(tableName, new Set(colVal, new Where(condition, "update")));
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

