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

class Context {

    private Map<String, List<Row>> tables = new HashMap<>();
    private ArrayList<String> schema;
    
    public Context(Map<String, List<Row>>newTables, ArrayList<String> newSchema) {
    	tables = newTables;
    	schema = newSchema;
    }

    private String table;
    private String column;
    private String condition;

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

    /**
     * Clears the context to defaults.
     * No filters, match all columns.
     */
    void clear() {
        column = "";
        columnMapper = matchAllColumns;
        whereFilter = matchAnyString;
    }

    List<String> search() {

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
    	int colIndex;
    	String[] splittedCondition = condition.split("=");
    	colIndex = schema.indexOf(splittedCondition[0]);
        whereFilter = s -> {
            String[] tmp = s.split(" ");
            return tmp[colIndex].equals(splittedCondition[1]);
        };
    }
}