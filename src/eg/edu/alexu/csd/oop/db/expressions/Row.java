package eg.edu.alexu.csd.oop.db.expressions;
import java.util.ArrayList;

class Row {
	
    private ArrayList<String> cols;
    Row(ArrayList<String> newCols){
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