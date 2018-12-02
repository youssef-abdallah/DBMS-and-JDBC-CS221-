package eg.edu.alexu.csd.oop.db.cs32.expressions;
import java.util.List;

class Select implements Expression {

    private String column;
    private From from;

    Select(String column, From from) {
    	column = column.replaceAll(" ", "");
        this.column = column;
        this.from = from;
    }

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setColumn(column);
        return from.interpret(ctx);
    }
}