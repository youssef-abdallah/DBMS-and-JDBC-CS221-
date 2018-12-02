package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.HashMap;
import java.util.Map;

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
			if (condition.contains("or") || condition.contains("and") || condition.contains("not"))
				condition = condition.replaceAll("\\s+=\\s+", "");
			else {
				condition = condition.replaceAll(" ", "");
			}
		}
		Expression exp = null;
		if(operationName.equalsIgnoreCase("select")) {
			String columns = null;
			if (colVal == null) {
				columns = "*";
			} else {
				StringBuilder sb = new StringBuilder();
				for(Map.Entry<String, String> entry : colVal.entrySet()) {
					sb.append(entry.getValue() + ",");
				}
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length() - 1);
				}
				columns = sb.toString();
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

