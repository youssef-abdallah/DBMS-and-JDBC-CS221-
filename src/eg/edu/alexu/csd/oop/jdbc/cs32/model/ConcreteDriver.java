package eg.edu.alexu.csd.oop.jdbc.cs32.model;
import java.io.File;

import org.apache.log4j.Logger;
import java.sql.*;
import java.util.Properties;

import eg.edu.alexu.csd.oop.jdbc.cs32.model.connectionsPool.ConnectionManager;


public class ConcreteDriver implements Driver {
	private static final Logger log = Logger.getLogger(Driver.class);

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		log.info("Trying to connect");
		File dir = (File) info.get("path");
		String path = dir.getAbsolutePath();
		return (Connection) ConnectionManager.getInstance(ConcreteConnection.class, path).acquire();
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return true;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		DriverPropertyInfo[] properties = new DriverPropertyInfo[1];
		DriverPropertyInfo property = new DriverPropertyInfo("path", info.getProperty("path"));
		properties[0] = property;
		return properties;
	}

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean jdbcCompliant() {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
	}

}
