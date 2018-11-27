package eg.edu.alexu.csd.oop.db.expressions;

import java.util.HashMap;
import java.util.Map;

public class ExpressionsFactory {
	
	public Expression ExpressionFactory(String operationName, 
			String tableName, String condition, Map<Object, Object> colVal) {
		Expression exp = null;
		if(operationName.equalsIgnoreCase("select")) {
			String columns = colVal.keySet().toString();
			if(condition.equals(null)) {
				exp = new Select(columns, new From(tableName));
			}else {
				exp = new Select(columns, new From(tableName, new Where(condition, "select")));
			}
		} else if(operationName.equalsIgnoreCase("insert")) {
			exp = new Insert(tableName, new Values((HashMap)colVal));
		} else if(operationName.equalsIgnoreCase("update")) {
			exp = new Update(tableName, new Set((HashMap)colVal, new Where(condition, "update")));
		} else if(operationName.equalsIgnoreCase("delete")) {
			
		}
		
		return exp;
	}
}

