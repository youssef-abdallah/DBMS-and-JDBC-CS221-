package eg.edu.alexu.csd.oop.db.cre_del;

import java.io.File;
import java.io.IOException;

public class Drop {

	public void dropDatabase(File files) throws IOException {

		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				dropDatabase(file);
			} else {
				if (!file.delete()) {
					throw new IOException();
				}
			}
		}
		if (!files.delete()) {
			throw new IOException();
		}
	}
}
