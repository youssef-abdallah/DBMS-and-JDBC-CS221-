package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ContextAdaptee {
	private Map<String, List<Row>> tables;
	private String table;
	private ArrayList<String> schema;
	private Predicate<String> whereFilter;
	private HashMap<String,String> setStatement;
	
	public ContextAdaptee(Map<String, List<Row>> tables, String table,
			ArrayList<String> schema) {
		this.tables = tables;
		this.table = table;
		this.schema = schema;
	}
	public void setWhereFilter(Predicate<String> whereFilter) {
		this.whereFilter = whereFilter;
	}
	public void setSetStatement(HashMap<String, String> setStatement) {
		this.setStatement = setStatement;
	}
	public int update() {
		int count = 0;
		for (Map.Entry<String, List<Row>> entry : tables.entrySet()) {
        	if (entry.getKey().equalsIgnoreCase(table)) {
        		List<Row> currentTable = entry.getValue();
        		for (int row = 0; row < currentTable.size(); row++) {
        			if (whereFilter.test(currentTable.get(row).toString())) {
        				count++;
        				ArrayList<String> currentColumns = currentTable.get(row).getCols();
        				for(int i = 0; i < currentColumns.size(); i++) {
        					String key = schema.get(i);
        					if (setStatement.containsKey(key)) {
        						currentTable.get(row).set(i, setStatement.get(key));
        					}
        				}
        			}
        		}
        	}
        }
		return count;
	}
	
	public int insert() {
    	for(Map.Entry<String, List<Row>> entry : tables.entrySet()) {
    		if(entry.getKey().equalsIgnoreCase(table)) {
    			List<Row> currentTable = entry.getValue();
    			ArrayList<String> rowContent = new ArrayList<>();
    			if(setStatement.containsKey("0")) {
    				for(int i = 0; i < schema.size(); i++) {
    					rowContent.add(setStatement.get(String.valueOf(i)));
    				}
    			} else {
    				for(int i = 0; i < schema.size(); i++) {
    					if(setStatement.containsKey(schema.get(i).toLowerCase())) {
    						rowContent.add(setStatement.get(schema.get(i)));
    					}else {
    						rowContent.add("null");
    					}
    				}
    			}
    			Row newRow = new Row(rowContent);
    			currentTable.add(newRow);
    			entry.setValue(currentTable);
    		}
    	}
		return 1;
	}
	
	public int delete() {
		int count = 0;
        for (Map.Entry<String, List<Row>> entry : tables.entrySet()) {
        	if (entry.getKey().equalsIgnoreCase(table)) {
        		List<Row> currentTable = entry.getValue();
        		for (int row = 0; row < currentTable.size(); row++) {
        			if (whereFilter.test(currentTable.get(row).toString())) {
        				count++;
        				currentTable.remove(currentTable.get(row));
        				row--;
        			}
        		}
        	}
        }
		return count;
	}
}
