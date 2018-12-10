package eg.edu.alexu.csd.oop.db.cs32.files;
import eg.edu.alexu.csd.oop.db.cs32.expressions.*;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xml {

	public boolean Write(String DataBase, String TableName, Object[][] Data, String operation, List<String> types) throws SQLException {
		if (!(Data == null)) {
			check(Data, types);
		}
		File file = new File(DataBase + System.getProperty("file.separator") + TableName + ".xml");
		if (file.exists() && operation.equals("create")) {
			return false;
		}
		DTD d = new DTD();
		List<String> ColumnNames = d.read(DataBase, TableName);
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Attr attr;
			Element root = document.createElement(TableName);
			document.appendChild(root);
			if (!(Data == null)) {
				for (int i = 0; i < Data.length; i++) {
					Element row = document.createElement("row");
					root.appendChild(row);
					attr = document.createAttribute("id");
					attr.setValue(String.valueOf(i));
					row.setAttributeNode(attr);
					for (int j = 0; j < ColumnNames.size(); j++) {
						Element element = document.createElement(ColumnNames.get(j));
						element.appendChild(document.createTextNode((String) Data[i][j]));
						row.appendChild(element);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			DOMSource domSource = new DOMSource(document);
			File file2 = new File(DataBase + System.getProperty("file.separator") + TableName + ".xml");
			if (!file2.exists()) {
				file2.createNewFile();
			}
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(domSource, streamResult);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}

	public List<Row> ReadAllFile(String DataBase, String TableName) {
		DTD d = new DTD();
		List<String> ColumnNames = d.read(DataBase, TableName);
		List<Row> result = new ArrayList<Row>();
		try {
			File fXmlFile = new File(DataBase + System.getProperty("file.separator") + TableName + ".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("row");
			ArrayList<String> temp;
			for (int i = 0; i < nList.getLength(); i++) {
				temp = new ArrayList<>();
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					for (int j = 0; j < ColumnNames.size(); j++) {
						temp.add(eElement.getElementsByTagName(ColumnNames.get(j)).item(0).getTextContent());
					}
				}
				result.add(new Row(temp));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	public HashMap<String, List<Row>> getTables(String DataBase) {
		HashMap<String, List<Row>> result = new HashMap<>();
		File f = new File(DataBase);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
		for (int i = 0; i < names.size(); i++) {
			if((names.get(i).contains(".xml"))){
				List<Row> temp = ReadAllFile(DataBase, (names.get(i)).substring(0, ((names.get(i)).length()) - 4));
				result.put((names.get(i)).substring(0, ((names.get(i)).length()) - 4), temp);
			}
		}
		
		return result;
	}
	
	private void check(Object[][] Data, List<String> types) throws SQLException {
		for (int i = 0; i < Data.length; i++) {
			for (int j = 0; j < types.size(); j++) {
				switch (types.get(j)) {
				case "varchar":
					if(!(((String) Data[i][j]).startsWith("'") && ((String) Data[i][j]).endsWith("'"))) {
						throw new SQLException();
					}
					break;
				case "int":
					if(((String) Data[i][j]).startsWith("'") && ((String) Data[i][j]).endsWith("'")) {
						throw new SQLException();
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	public List<String> getTypes(String DataBase, String TableName){
		List<String> result = new ArrayList<String>();
		DTD d = new DTD();
		List<String> ColumnNames = d.read(DataBase, TableName);
		try {
			File fXmlFile = new File(DataBase + System.getProperty("file.separator") + TableName + ".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("row");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					for (int j = 0; j < ColumnNames.size(); j++) {
						if (i == 0 && eElement.getElementsByTagName(ColumnNames.get(j)).item(0).getTextContent().contains("'")) {
							result.add("varchar");
						} else if ((i == 0 && !eElement.getElementsByTagName(ColumnNames.get(j)).item(0).getTextContent().contains("'"))) {
							result.add("int");
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
		
	}

}