package eg.edu.alexu.csd.oop.db.expressions;

import java.util.List;

class From implements Expression {

    private String table;
    private Where where;
    private String operation;

    From(String table) {
        this.table = table;
    }

    From(String table, Where where) {
        this.table = table;
        this.where = where;
    }

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setTable(table);
        if (where == null) {
            return ctx.search();
        }
        return where.interpret(ctx);
    }
}