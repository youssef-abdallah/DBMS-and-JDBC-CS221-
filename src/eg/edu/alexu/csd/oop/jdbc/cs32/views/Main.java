package eg.edu.alexu.csd.oop.jdbc.cs32.views;

import eg.edu.alexu.csd.oop.jdbc.cs32.model.ConcreteDriver;
import java.sql.*;

public class Main {
	public static void main(String[] args) {
		Driver concreteDriver = new ConcreteDriver();
		new UserInterface(concreteDriver);
	}
}
