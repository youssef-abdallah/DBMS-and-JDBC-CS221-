import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class DTD {

	public void Write(String DataBase, String TableName, List<String> ColumnNames) {
		try {
			File file = new File(DataBase + TableName + ".dtd");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter write = new BufferedWriter(fw);
			write.write("<! ELEMENT " + TableName + " (row)*>");
			write.newLine();
			write.write("<! ELEMENT row (");
			for (int i = 0; i < ColumnNames.size() - 1; i++) {
				write.write(ColumnNames.get(i) + ",");
			}
			write.write(ColumnNames.get(ColumnNames.size() - 1) + ")>");
			write.newLine();
			for (int i = 0; i < ColumnNames.size() - 1; i++) {
				write.write("<! ELEMENT " + ColumnNames.get(i) + " (#PCDATA)>");
				write.newLine();
			}
			write.write("<! ELEMENT " + ColumnNames.get(ColumnNames.size() - 1) + " (#PCDATA)>");
			write.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public ArrayList<String> read(String DataBase, String TableName){
		ArrayList<String> result = new ArrayList<String>();
		try {
			File file = new File(DataBase + TableName + ".dtd");
			FileReader fr = new FileReader(file);
			BufferedReader r = new BufferedReader(fr);
			String str;
			str = r.readLine();
			str = r.readLine();
			String[] line;
			while (r.read()!=-1) {
				str = r.readLine();
				line = str.split(" ");
				result.add(line[2]);
			}
			r.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
}