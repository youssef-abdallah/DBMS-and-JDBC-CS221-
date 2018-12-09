package eg.edu.alexu.csd.oop.db.cs32.expressions;

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
    private static final Predicate<String> matchAnyString = s -> s.length() > 0;
    private static final Function<String, Stream<? extends String>> matchAllColumns = Stream::of;
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
    	ContextAdaptee adaptee = new ContextAdaptee(tables, table, schema);
    	adaptee.setSetStatement(setStatement);
    	adaptee.setWhereFilter(whereFilter);
    	int length = adaptee.update();
        clear();
        List<String> result = new ArrayList<String>();
        for(int i = 0; i < length; i++)
        	result.add("");
        return result;
    }
    
    public List<String> insert() {
    	ContextAdaptee adaptee = new ContextAdaptee(tables, table, schema);
    	adaptee.setSetStatement(setStatement);
    	int length = adaptee.insert();
    	List<String> result = new ArrayList<String>();
    	for(int i = 0; i < length; i++)
        	result.add("");
    	clear();
    	return result;
    }
    
    public List<String> delete() {
        ContextAdaptee adaptee = new ContextAdaptee(tables, table, schema);
        adaptee.setWhereFilter(whereFilter);
        int length = adaptee.delete();
    	List<String> result = new ArrayList<String>();
    	for(int i = 0; i < length; i++)
        	result.add("");
        clear();
        return result;
    }
    private void setColumnMapper() {
    	List<Integer> colIndexes = new ArrayList<Integer>();
    	if (column.equalsIgnoreCase("*")) {
    		return;
    	}
    	String[] columns = column.split(",");
    	for(int i = 0; i < columns.length; i++) {
    		if (schema.contains(columns[i])) {
    			colIndexes.add(schema.indexOf(columns[i]));
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
            if (sb.length() != 0)
            	sb.deleteCharAt(sb.length() - 1);
            return Stream.of(sb.toString());
        };
    }
    
    private void setRowMapper() {
    	colIndex = -1;
    	String[] conditions = condition.split("\\s+[aA][nN][dD]\\s+|\\s+[oO][rR]\\s+");
    	String operator = "";
    	int idx = 0;
    	boolean negated = false;
    	if (conditions[idx].contains("not")) {
    		conditions[idx] = conditions[idx].replaceAll("[nN][oO][tT]\\s+", "");
    		negated = true;
    	}
    	if (conditions[idx].contains("=")) {
    		operator = "=";
    	} else if (conditions[idx].contains(">")) {
    		operator = ">";
    	} else if (conditions[idx].contains("<")) {
    		operator = "<";
    	}
    	String[] splittedCondition = conditions[idx].split(operator);
    	for (int i = 0; i < schema.size(); i++) {
    		if (schema.get(i).equalsIgnoreCase(splittedCondition[0])) {
    			colIndex = i;
    			break;
    		}
    	}
    	final int x = colIndex;
    	final int z = idx;
        whereFilter = s -> {
            String[] tmp = s.split(" ");
            if (conditions[z].contains("=")) {
            	return tmp[x].equalsIgnoreCase(splittedCondition[1]);
            }
            else if (conditions[z].contains(">")) {
            	return Integer.parseInt(tmp[x]) > Integer.parseInt(splittedCondition[1]);
            } else {
            	return Integer.parseInt(tmp[x]) < Integer.parseInt(splittedCondition[1]);
            }
            	
        };
        if (negated) {
        	whereFilter = whereFilter.negate();
        	negated = false;
        }
        ArrayList<String> andOrArray = new ArrayList<>();
        for (int i = 0; i < condition.length(); i++) {
        	if (condition.toLowerCase().indexOf("and", i) != -1) {
        		andOrArray.add("and");
        		i = condition.toLowerCase().indexOf("and", i);
        	}
        	else if (condition.toLowerCase().indexOf("or", i) != -1) {
        		andOrArray.add("or");
        		i = condition.toLowerCase().indexOf("or", i);
        	}
        }
        int operatorIndex = 0;
        for(int i = idx + 1; i < conditions.length; i++) {
        	if (conditions[i].contains("not")) {
        		negated = true;
        		conditions[i].replaceAll("[nN][oO][tT]\\s+", "");
        	}
        	Predicate<String> newFilter;
        	if (conditions[i].contains("=")) {
        		operator = "=";
        	} else if (conditions[i].contains(">")) {
        		operator = ">";
        	} else if (conditions[i].contains("<")) {
        		operator = "<";
        	}
        	String[] splittedCondition2 = conditions[i].split(operator);
        	for (int k = 0; k < schema.size(); k++) {
        		if (schema.get(k).equalsIgnoreCase(splittedCondition2[0])) {
        			colIndex = k;
        			break;
        		}
        	}
        	final int y = colIndex;
        	final int w = i;
            newFilter = s -> {
                String[] tmp = s.split(" ");
                if (conditions[w].contains("=")) {
                	return tmp[y].equalsIgnoreCase(splittedCondition2[1]);
                }
                else if (conditions[w].contains(">")) {
                	return Integer.parseInt(tmp[y]) > Integer.parseInt(splittedCondition2[1]);
                } else {
                	return Integer.parseInt(tmp[y]) < Integer.parseInt(splittedCondition2[1]);
                }
                	
            };
            if (andOrArray.get(operatorIndex).equalsIgnoreCase("and")) {
            	if (!negated) {
            		whereFilter = whereFilter.and(newFilter);
            	} else {
            		whereFilter = whereFilter.and(newFilter.negate());
            		negated = false;
            	}
            } else {
            	if (!negated) {
            		whereFilter = whereFilter.or(newFilter);
            	} else {
            		whereFilter = whereFilter.or(newFilter.negate());
            		negated = false;
            	}
            }
            operatorIndex++;
        }
        
    }
}