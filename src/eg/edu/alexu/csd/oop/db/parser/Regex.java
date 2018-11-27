package eg.edu.alexu.csd.oop.db.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	private HashMap<String, Object> map = new HashMap<String, Object>();
	private HashMap<Object, String> colMap = new HashMap<Object, String>();
	private ArrayList<String> list = new ArrayList<>();

	public boolean validate(String str2check) {
		String regex = "[a-zA-Z]+\\s+(\\*\\s)?[a-zA-Z,0-9]+\\s+[a-zA-Z <0-9_>='(),]+\\s*";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(str2check);
		return m.matches();
	}

	private boolean validate(String regex, String str2check) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(str2check);
		return m.matches();
	}

	private String getGroupFromQuery(String regex, String str2check, int num) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(str2check);
		if (m.find()) {
			return m.group(num);
		}
		return null;
	}

	private void fillColMap(String regex, String str2check) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(str2check);
		while (m.find()) {
			colMap.put(m.group(1), m.group(2));
		}
		map.put("colMap", colMap);
	}

	private void fillValuesMap(String regex, String str2check, boolean flag) {// if true values if false columns
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(str2check);
		if (flag) {
			if (list.isEmpty()) {
				int i = 0;
				while (m.find()) {
					colMap.put(i, m.group(2));
					i++;
				}
			} else {
				int i = 0;
				while (m.find()) {
					colMap.put(list.get(i), m.group(2));
					i++;
				}
			}
			map.put("colMap", colMap);
		} else {
			while (m.find()) {
				list.add(m.group(2));
			}
		}

	}

	private void parseDelete(String query) {
		String regex1 = "delete\\s+from\\s+([a-zA-Z0-9_]+)\\s*";
		String regex2 = regex1.concat("where\\s+([a-zA-Z=><'0-9 ]+)\\s*");
		if (validate(regex2, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", getGroupFromQuery(regex2, query, 2));
		} else if (validate(regex1, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", null);
		}
	}

	private void parseUpdate(String query) {
		String regex1 = "update\\s+([a-zA-Z0-9_]+)\\s+set\\s+([a-zA-Z0-9,_]+\\s*=\\s*[a-zA-Z0-9,_ ']+\\s)+\\s*";
		String regex2 = regex1.concat("where\\s+([a-zA-Z=><'0-9 ]+)\\s*");
		if (validate(regex2, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", getGroupFromQuery(regex2, query, 3));
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*(\\'[a-zA-Z0-9_ ]+\\')", query);
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*([a-zA-Z0-9_]+)", query);
		} else if (validate(regex1, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			map.put("where", null);
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*(\\'[a-zA-Z0-9_ ]+\\')", query);
			fillColMap("[^WHERE]\\s([a-zA-Z0-9_]+)\\s*=\\s*([a-zA-Z0-9_]+)", query);
		}

	}

	private void parseInsert(String query) {// problem with strings with spaces
		String regex1 = "insert\\s+into\\s+([a-zA-z0-9_]+)\\s+[(]((\\s*([a-zA-Z0-9_']+)\\s*\\,?)+)[)]\\s*values\\s+[(]((\\s*([a-zA-Z0-9_'.]+)\\s*\\,?)+)[)]\\s*";
		String regex2 = "insert\\s+into\\s+([a-zA-z0-9_]+)\\s+values\\s+[(]((\\s*([a-zA-Z0-9_'.]+)\\s*\\,?)+)[)]\\s*";
		if (validate(regex1, query)) {
			map.put("tableName", getGroupFromQuery(regex1, query, 1));
			fillValuesMap("(\\s*([a-zA-Z0-9_]+)\\s*\\,?)", getGroupFromQuery(regex1, query, 2), false);
			fillValuesMap("(\\s*([a-zA-Z0-9_'.]+)\\s*\\,?)", getGroupFromQuery(regex1, query, 5), true);
		} else if (validate(regex2, query)) {
			map.put("tableName", getGroupFromQuery(regex2, query, 1));
			fillValuesMap("(\\s*([a-zA-Z0-9_'.]+)\\s*\\,?)", getGroupFromQuery(regex2, query, 2), true);
		}
	}

	public Map<String, Object> parseQuery(String query) {
		list.clear();
		String operation = getGroupFromQuery("([a-zA-Z]{6})\\s", query, 1);
		switch (operation.toUpperCase()) {
		case ("SELECT"):
			break;
		case ("UPDATE"):
			map.put("operation", "UPDATE");
			parseUpdate(query);
			break;
		case ("INSERT"):
			map.put("operation", "INSERT");
			parseInsert(query);
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