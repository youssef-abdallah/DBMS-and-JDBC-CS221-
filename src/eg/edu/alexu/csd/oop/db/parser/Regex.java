package eg.edu.alexu.csd.oop.db.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	private static HashMap<String, Object> map = new HashMap<String, Object>();
	private static HashMap<String, String> colMap = new HashMap<String, String>();

	public boolean validate(String str2check) {
		String regex = "[a-zA-Z]+\\s+(\\*\\s)?[a-zA-Z,0-9]+\\s+[a-zA-Z <0-9_>='(),]+\\s*";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str2check);
		return m.matches();
	}

	private boolean validate(String regex, String str2check) {
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str2check);
		return m.matches();
	}

	private String getGroupFromQuery(String regex, String str2check, int num) {
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str2check);
		if (m.find()) {
			return m.group(num);
		}
		return null;
	}

	private void fillColMap(String regex, String str2check) {
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str2check);
		while (m.find()) {
			colMap.put(m.group(1), m.group(2));
		}
		map.put("colMap", colMap);
	}

	private void parseDelete(String query) {
		String regex1 = "[a-zA-Z]{6}\\s+[a-zA-Z]{4}\\s+([a-zA-Z0-9_]+)\\s*";
		String regex2 = regex1.concat("[a-zA-Z]{5}\\s+([a-zA-Z=><'0-9 ]+)\\s*");
		if (validate(regex2, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", getGroupFromQuery(regex2, query, 2));
		} else if (validate(regex1, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", "ALL");
		}
	}

	private void parseUpdate(String query) {
		String regex1 = "[a-zA-Z]{6}\\s+([a-zA-Z0-9_]+)\\s+[a-zA-Z]{3}\\s+([a-zA-Z0-9,_]+\\s*=\\s*[a-zA-Z0-9,_ ']+\\s)+\\s*";
		String regex2 = regex1.concat("[a-zA-Z]{5}\\s+([a-zA-Z=><'0-9 ]+)\\s*");
		if (validate(regex2, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", getGroupFromQuery(regex2, query, 3));
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*(\\'[a-zA-Z0-9_ ]+\\')", query);
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*([a-zA-Z0-9_]+)", query);
		} else if (validate(regex1, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", "ALL");
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*(\\'[a-zA-Z0-9_ ]+\\')", query);
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*([a-zA-Z0-9_]+)", query);
		}

	}

	public Map<String, Object> parseQuery(String query) {
		String operation = getGroupFromQuery("([a-zA-Z]{6})\\s", query, 1);
		switch (operation.toUpperCase()) {
		case ("SELECT"):
			break;
		case ("UPDATE"):
			map.put("operation", "UPDATE");
			parseUpdate(query);
			break;
		case ("INSERT"):
			break;
		case ("DELETE"):
			map.put("operation", "DELETE");
			parseDelete(query);
			break;
		default:
			System.out.println("wrong first word in query");
			break;
		}
		return map;
	}

}