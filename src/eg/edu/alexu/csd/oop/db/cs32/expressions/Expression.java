package eg.edu.alexu.csd.oop.db.cs32.expressions;
import java.util.List;

public interface Expression {
	List<String> interpret(Context ctx);
}
