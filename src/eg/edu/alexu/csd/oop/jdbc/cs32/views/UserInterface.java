package eg.edu.alexu.csd.oop.jdbc.cs32.views;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class UserInterface {
	private Driver driver;
	private Connection connection;
	
	public UserInterface(Driver driver) {
		this.driver = driver;
		startExecution();
	}
	
	private void startExecution() {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Properties info = new Properties();
		try {
			File dbDir = new File("Databases");
	        info.put("path", dbDir.getAbsoluteFile());
			connection = driver.connect("jdbc:xmldb://localhost", info);
		} catch (SQLException e1) {
			System.out.println("Failed to connect");
		}
		while(true) {
			System.out.println("Enter a batch of sql queries");
			try {
				Statement statement = connection.createStatement();
				String query;
				query = sc.nextLine();
				statement.execute(query);
				if (query.contains("select")) {
					ResultSet resultSet = statement.executeQuery(query);
					System.out.println("Specify the row");
					try {
						int row = Integer.valueOf(sc.nextLine());
						System.out.println("Specify the column");
						int col = Integer.valueOf(sc.nextLine());
						resultSet.absolute(row);
						Object value = resultSet.getObject(col);
						System.out.println(value);
					} catch(Exception e) {
						System.out.println("You didn't enter a number");
					}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
