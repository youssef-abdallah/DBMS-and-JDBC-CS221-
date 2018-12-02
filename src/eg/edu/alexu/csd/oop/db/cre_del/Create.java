package eg.edu.alexu.csd.oop.db.cre_del;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Create {

	public boolean createDatabase(String path) {
		boolean checkDir = false;
		try {
			Path xpath = Paths.get(path);
			checkDir = Files.exists(xpath);
			if (checkDir) {
			} else {
				Files.createDirectory(xpath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkDir;
	}
	
}
