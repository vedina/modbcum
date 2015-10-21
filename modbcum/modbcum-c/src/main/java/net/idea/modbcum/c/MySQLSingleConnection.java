package net.idea.modbcum.c;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;

public class MySQLSingleConnection<CONTEXT> extends
		DBConnectionConfigurable<CONTEXT> {

	public MySQLSingleConnection(CONTEXT context, String configFile) {
		super(context, configFile);
	}

	public synchronized Connection getConnection(String connectionURI)
			throws AmbitException, SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		return DriverManager.getConnection(connectionURI);
	}
}