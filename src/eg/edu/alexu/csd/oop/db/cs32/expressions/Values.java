package eg.edu.alexu.csd.oop.db.cs32.expressions;

import java.util.HashMap;
import java.util.List;

public class Values implements Expression{
	
	private HashMap<String, String> colVal;
	
	public Values(HashMap<String, String> colVal) {
		this.colVal = colVal;
	}

	@Override
	public List<String> interpret(Context ctx) {
		ctx.setSetStatement(colVal);
		return ctx.insert();
	}

}
