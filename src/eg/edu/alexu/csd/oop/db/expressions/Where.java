package eg.edu.alexu.csd.oop.db.expressions;
import java.util.List;

class Where implements Expression {

    private String condition;
    private String operation;

    Where(String condition) {
        this.condition = condition;
    }

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setCondition(condition);
        return ctx.search();
    }
}