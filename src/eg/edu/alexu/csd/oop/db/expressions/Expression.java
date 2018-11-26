package eg.edu.alexu.csd.oop.db.expressions;
import java.util.List;

public interface Expression {
	List<String> interpret(Context ctx);
}
