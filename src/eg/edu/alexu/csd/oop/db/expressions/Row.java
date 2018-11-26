package eg.edu.alexu.csd.oop.db.expressions;
import java.util.ArrayList;

public class Row {
	
    private ArrayList<String> cols;
    public Row(ArrayList<String> newCols){
    	cols = newCols;
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	cols.forEach(x -> sb.append(x + " "));
    	sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}