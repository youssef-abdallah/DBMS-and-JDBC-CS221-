package eg.edu.alexu.csd.oop.db.expressions;

import java.util.HashMap;
import java.util.List;

public class Set implements Expression {
	private HashMap<String, String> setStatement;
	private Where where;
	
	public Set(HashMap<String, String> set, Where where) {
		this.setStatement = set;
		this.where = where;
	}

	@Override
	public List<String> interpret(Context ctx) {
		ctx.setSetStatement(setStatement);
        if (where == null) {
            return ctx.update();
        }
        return where.interpret(ctx);
	}

}
