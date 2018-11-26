import java.io.File;
import java.util.ArrayList;
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

	public void Write(String DataBase, String TableName, Object[][] Data) {
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
			File file = new File(DataBase + TableName + ".xml");
			if (!file.exists()) {
				file.createNewFile();
			}
			StreamResult streamResult = new StreamResult(file);
			transformer.transform(domSource, streamResult);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public List<Row> ReadAllFile(String DataBase, String TableName) {
		DTD d = new DTD();
		List<String> ColumnNames = d.read(DataBase, TableName);
		List<Row> result = new ArrayList<Row>();
		try {
			File fXmlFile = new File(DataBase + TableName + ".xml");
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

}
