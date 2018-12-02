package eg.edu.alexu.csd.oop.db.cs32.expressions;
import java.util.List;

class Where implements Expression {

    private String condition;
    private String operation;

    Where(String condition, String operation) {
        this.condition = condition;
        this.operation = operation;
    }

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setCondition(condition);
        if (operation.equalsIgnoreCase("select")) {
        	return ctx.search();
        } else if (operation.equalsIgnoreCase("delete")) {
        	return ctx.delete();
        }
        else {
        	return ctx.update();
        }
    }
}