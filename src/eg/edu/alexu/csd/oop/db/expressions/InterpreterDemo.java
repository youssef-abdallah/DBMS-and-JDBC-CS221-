package eg.edu.alexu.csd.oop.db.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class InterpreterDemo {

    public static void main(String[] args) {
    	Object[][] ans;
    	List<Row> list = new ArrayList<>();
    	HashMap<String, List<Row>> tables = new HashMap<String, List<Row>>();
        /*list.add(new Row("John", "Doe"));
        list.add(new Row("Jan", "Kowalski"));
        list.add(new Row("Dominic", "Doom"));*/
        ArrayList<String> a1 = new ArrayList<String>();
        a1.add("John");
        a1.add("Doe");
        ArrayList<String> a2 = new ArrayList<String>();
        a2.add("Jan");
        a2.add("Kowalski");
        ArrayList<String> a3 = new ArrayList<String>();
        a3.add("Dominic");
        a3.add("Doom");
        list.add(new Row(a1));
        list.add(new Row(a2));
        list.add(new Row(a3));
        tables.put("people", list);

        Expression query = new Select("name", new From("people"));
        ArrayList<String> schema = new ArrayList<String>();
        schema.add("name");
        schema.add("surname");
        Context ctx = new Context(tables, schema);
        List<String> result = query.interpret(ctx);
        System.out.println(result);

        Expression query2 = new Select("*", new From("people"));
        List<String> result2 = query2.interpret(ctx);
        String[] s = result2.get(0).split(" ");
        int rows = result2.size();
        int cols = s.length;
        /*ans = new Object[rows][cols];
        for(int i = 0; i < rows; i++) {
        	String[] tmp = result2.get(i).split(" ");
        	for(int j = 0; j < cols; j++) {
        		ans[i][j] = tmp[j];
        		System.out.print(ans[i][j] + " ");
        	}
        	System.out.println();
        }*/
        System.out.println(result2);

        Expression query3 = new Select("name", new From("people", new Where("surname=Doe")));
        List<String> result3 = query3.interpret(ctx);
        System.out.println(result3);
    }
}