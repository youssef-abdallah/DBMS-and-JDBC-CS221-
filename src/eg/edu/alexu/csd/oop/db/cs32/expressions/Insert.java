package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.List;

public class Insert implements Expression{
	
	private String table;
	private Values values;
	
	public Insert(String table, Values values) {
		this.table = table;
		this.values = values;
	}

	@Override
	public List<String> interpret(Context ctx) {
		ctx.setTable(table);
		return values.interpret(ctx);
	}

}
