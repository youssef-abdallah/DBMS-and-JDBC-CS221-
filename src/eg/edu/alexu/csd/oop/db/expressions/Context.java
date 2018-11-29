package eg.edu.alexu.csd.oop.db.expressions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Context {

    private Map<String, List<Row>> tables = new HashMap<>();
    private ArrayList<String> schema;
    
    public Context(Map<String, List<Row>>newTables, ArrayList<String> newSchema) {
    	tables = newTables;
    	schema = newSchema;
    }
    private int colIndex;
    private String table;
    private String column;
    private String condition;
    private HashMap<String, String> setStatement = new HashMap<>();

    /**
     * Default setup, used for clearing the context for next queries.
     * See {@link Context#clear()}
     */
    private static final Predicate<String> matchAnyString = s -> s.length() > 0;
    private static final Function<String, Stream<? extends String>> matchAllColumns = Stream::of;
    /**
     * Varies based on setup in subclasses of {@link Expression}
     */
    private Predicate<String> whereFilter = matchAnyString;
    private Function<String, Stream<? extends String>> columnMapper = matchAllColumns;

    void setColumn(String column) {
        this.column = column;
        setColumnMapper();
    }

    void setTable(String table) {
        this.table = table;
    }

    void setCondition(String condition) {
    	this.condition = condition;
    	setRowMapper();
    }
    
    void setSetStatement(HashMap<String, String> setStatement) {
        this.setStatement = setStatement;
    }

    /**
     * Clears the context to defaults.
     * No filters, match all columns.
     */
    void clear() {
    	if (setStatement.size() != 0) {
    		setStatement.clear();
    	}
        column = "";
        columnMapper = matchAllColumns;
        whereFilter = matchAnyString;
    }

    public List<String> search() {

        List<String> result = tables.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(table))
                .flatMap(entry -> Stream.of(entry.getValue()))
                .flatMap(Collection::stream)
                .map(Row::toString)
                .filter(whereFilter)
                .flatMap(columnMapper)
                .collect(Collectors.toList());

        clear();

        return result;
    }
    
    public List<String> update() {

        List<String> result = new ArrayList<String>();       
        for (Map.Entry<String, List<Row>> entry : tables.entrySet()) {
        	if (entry.getKey().equalsIgnoreCase(table)) {
        		List<Row> currentTable = entry.getValue();
        		for (int row = 0; row < currentTable.size(); row++) {
        			if (whereFilter.test(currentTable.get(row).toString())) {
        				result.add("");
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

        clear();
        return result;
    }
    
    public List<String> insert() {
    	
    	List<String> result = new ArrayList<String>();
    	for(Map.Entry<String, List<Row>> entry : tables.entrySet()) {
    		if(entry.getKey().equalsIgnoreCase(table)) {
    			List<Row> currentTable = entry.getValue();
    			ArrayList<String> rowContent = new ArrayList<>();
    			result.add("");
    		// if (all columns are given)
    			if(setStatement.containsKey("0")) {
    				for(int i = 0; i<schema.size(); i++) {
    					rowContent.add(setStatement.get(String.valueOf(i)));
    				}
    			} else {
    				for(int i = 0; i<schema.size(); i++) {
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
    	
    	clear();
    	return result;
    }
    
    public List<String> delete() {
    	List<String> result = new ArrayList<String>();       
        for (Map.Entry<String, List<Row>> entry : tables.entrySet()) {
        	if (entry.getKey().equalsIgnoreCase(table)) {
        		List<Row> currentTable = entry.getValue();
        		for (int row = 0; row < currentTable.size(); row++) {
        			if (whereFilter.test(currentTable.get(row).toString())) {
        				result.add("");
        				currentTable.remove(currentTable.get(row));
        				row--;
        			}
        		}
        	}
        }
        clear();
        return result;
    }

    /**
     * Sets column mapper based on {@link #column} attribute.
     * Note: If column is unknown, will remain to look for all columns.
     */
    private void setColumnMapper() {
    	List<Integer> colIndexes = new ArrayList<Integer>();
    	if (column.equalsIgnoreCase("*")) {
    		return;
    	}
    	String[] columns = column.split(",");
    	for(int i = 0; i < columns.length; i++) {
    		if (schema.contains(columns[i])) {
    			colIndexes.add(i);
    		}
    	}
        columnMapper = s -> {
            String[] tmp = s.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tmp.length; i++) {
            	if (colIndexes.contains(i)) {
            		sb.append(tmp[i] + " ");
            	}
            }
            sb.deleteCharAt(sb.length() - 1);
            return Stream.of(sb.toString());
        };
    }
    
    private void setRowMapper() {
    	colIndex = -1;
    	String operator = "";
    	if (condition.contains("=")) {
    		operator = "=";
    	} else if (condition.contains(">")) {
    		operator = ">";
    	} else if (condition.contains("<")) {
    		operator = "<";
    	}
    	String[] splittedCondition = condition.split(operator);
    	for (int i = 0; i < schema.size(); i++) {
    		if (schema.get(i).equalsIgnoreCase(splittedCondition[0])) {
    			colIndex = i;
    			break;
    		}
    	}
    	final int x = colIndex;
        whereFilter = s -> {
            String[] tmp = s.split(" ");
            if (condition.contains("="))
            	return tmp[x].equalsIgnoreCase(splittedCondition[1]);
            else if (condition.contains(">")) {
            	return Integer.parseInt(tmp[x]) > Integer.parseInt(splittedCondition[1]);
            } else {
            	return Integer.parseInt(tmp[x]) < Integer.parseInt(splittedCondition[1]);
            }
            	
        };
    }
}