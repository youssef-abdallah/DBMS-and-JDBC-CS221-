package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.List;

public class Delete implements Expression {
	
	private String table;
	private Where where;
	
	public Delete(String table){
		this.table = table;
	}
	public Delete(String table, Where where){
		this.table = table;
		this.where = where;
	}
	@Override
	public List<String> interpret(Context ctx) {
		ctx.setTable(table);
		if (where == null) {
			return ctx.delete();
		}
		return where.interpret(ctx);
	}

}