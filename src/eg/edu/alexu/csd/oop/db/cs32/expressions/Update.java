package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.List;
public class Update implements Expression {
	private String table;
	private Set set;
	
	public Update(String table, Set set) {
		this.table = table;
		this.set = set;
	}

	@Override
	public List<String> interpret(Context ctx) {
		ctx.setTable(table);
		return set.interpret(ctx);
	}

}

