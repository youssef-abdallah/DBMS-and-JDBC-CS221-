package eg.edu.alexu.csd.oop.db.facade;

import eg.edu.alexu.csd.oop.db.parser.Regex;

public class Facade {
	private Regex parser;
	private String query;
	
	public Facade(String query) {
		parser = new Regex();
		this.query = query;
	}
	
	public void evaluateQuery() {
		parser.parseQuery(query);
	}
}
