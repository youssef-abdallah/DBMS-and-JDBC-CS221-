package eg.edu.alexu.csd.oop.db.cs32.Main;

import java.sql.SQLException;
import java.util.Scanner;

import eg.edu.alexu.csd.oop.db.ConcreteDatabase;

public class DBMS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		Scanner scBoolean = new Scanner(System.in);
		ConcreteDatabase db = new ConcreteDatabase();
		while(true) {
			System.out.println("Enter a SQL query:");
			String query;
			query = sc.nextLine();
			String temp = query.toUpperCase();
			 
			if(temp.contains("CREATE DATABASE")) {
				String databaseName = query.substring(16, query.length());
				System.out.println("In case the entered name exists already\nEnter: "
						+ "\n'true' to create a new Database \n'false' to use the existing Database\n");
				boolean dropIfExists = scBoolean.nextBoolean();
				db.createDatabase(databaseName, dropIfExists);
			} else if(temp.contains("CREATE TABLE") || 
					temp.contains("DROP TABLE") || temp.contains("DROP DATABASE")) {
				try {
					 db.executeStructureQuery(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(temp.contains("INSERT INTO") || 
					temp.contains("UPDATE") || temp.contains("DELETE")) {
				try {
					int count = db.executeUpdateQuery(query);
					System.out.println("Number of updated rows: " + count);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(temp.contains("SELECT")) {
				try {
					Object result[][] = db.executeQuery(query);
					for(int i = 0; i < result.length; i++) {
						for(int j = 0; j < result[i].length; j++) {
							System.out.print(result[i][j] + " ");
						}
						System.out.println();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("You have enterd a wrong SQL query");
			}

		}
	}
	
}

