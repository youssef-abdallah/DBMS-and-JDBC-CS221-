package eg.edu.alexu.csd.oop.jdbc.cs32.model.connectionsPool;
import java.sql.Connection;
import java.sql.SQLException;

import eg.edu.alexu.csd.oop.jdbc.cs32.model.ConcreteConnection;

public class ConnectionManager extends ObjectPool<Connection> {
	private static Class<? extends Connection> concreteConnection;
	private static ConnectionManager uniqueInstance = new ConnectionManager();
	private static String path;
	
	
	public static ConnectionManager getInstance(Class<? extends Connection> concreteConnection
			,String path) {
		ConnectionManager.concreteConnection = concreteConnection;
		ConnectionManager.path = path;
		return uniqueInstance;
	}
	private ConnectionManager() {
		super();
	}

	@Override
	protected Connection create() {
		try {
			ConcreteConnection connection = (ConcreteConnection) concreteConnection.newInstance();
			connection.setPath(path);
			return connection;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean validate(Connection object) {
		try {
		      return !((Connection) object).isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		    return false;
		}
	}

	@Override
	public void expire(Connection object) {
		try {
		      ((Connection) object).close();
		} catch (SQLException e) {
		     e.printStackTrace();
		}
	}

}
