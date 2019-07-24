package net.idea.modbcum.c;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.config.ConfigProperties;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;

public class DBConnectionConfigurable<CONTEXT> {

	protected static ConfigProperties properties = new ConfigProperties();
	protected String configFile;

	protected LoginInfo loginInfo;

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}

	protected String namedConfig = null;

	public String getNamedConfig() {
		return namedConfig;
	}

	public void setNamedConfig(String namedConfig) {
		this.namedConfig = namedConfig;
	}

	public DBConnectionConfigurable(CONTEXT context, String configFile) {
		super();
		this.configFile = configFile;
		loginInfo = getLoginInfo(context);
	}

	/**
	 * 
	 * @return
	 */
	public boolean dataset_prefixed_compound_uri() {
		Object prefix = getProperty("dataset.members.prefix");
		try {
			return Boolean.parseBoolean(prefix.toString());
		} catch (Exception x) {
			return false;
		}
	}

	public LoginInfo getLoginInfo(CONTEXT context) {

		LoginInfo li = new LoginInfo();

		String p = properties.getPropertyWithDefault(Preferences.DATABASE, configFile, "ambit2");
		li.setDatabase(p == null || ("${ambit.db}".equals(p)) ? "ambit2" : p);
		p = properties.getPropertyWithDefault(Preferences.PORT, configFile, "3306");
		li.setPort(p == null ? "3306" : p);
		p = properties.getPropertyWithDefault(Preferences.USER, configFile, "guest");
		li.setUser(p == null ? "guest" : p);
		p = properties.getPropertyWithDefault(Preferences.PASSWORD, configFile, "guest");
		li.setPassword(p == null ? "guest" : p);
		p = properties.getPropertyWithDefault(Preferences.HOST, configFile, "localhost");
		li.setHostname(p == null || ("${ambit.db.host}".equals(p)) ? "localhost" : p);

		p = properties.getPropertyWithDefault(Preferences.DRIVERNAME, configFile, "com.mysql.jdbc.Driver");
		li.setDriverClassName(p == null || (p.toString().startsWith("${")) ? "com.mysql.jdbc.Driver" : p.toString());

		configurefromContext(li, context);

		return li;
	}

	protected void configurefromContext(LoginInfo li, CONTEXT context) {

	}

	protected String getConnectionURI() throws AmbitException {
		return getConnectionURI(null, null);
	}

	/*
	 * protected String getConnectionURI(Request request) throws AmbitException
	 * { if (request == null) return getConnectionURI(null,null); else if
	 * (request.getChallengeResponse()==null) return
	 * getConnectionURI(null,null); else try { return
	 * getConnectionURI(request.getChallengeResponse().getIdentifier(), new
	 * String(request.getChallengeResponse().getSecret())); } catch (Exception
	 * x) { return getConnectionURI(null,null); } }
	 */
	protected String getConnectionURI(String user, String password) throws AmbitException {

		try {
			LoginInfo li = loginInfo;
			return DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(),
					user == null ? li.getUser() : user, password == null ? li.getPassword() : password);
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}

	public synchronized Connection getConnection(String user, String password) throws AmbitException, SQLException {
		return getConnection(getConnectionURI(user, password));
	}

	public synchronized Connection getConnection() throws AmbitException, SQLException {
		return getConnection(5, false, 2);
	}

	public synchronized Connection getConnection(int maxRetry, boolean testIt, int testTimeout)
			throws AmbitException, SQLException {
		return getConnectionNamedConfig(getNamedConfig(), getConnectionURI(), maxRetry, testIt, testTimeout);
	}

	public synchronized Connection getConnection(String namedConfig) throws AmbitException, SQLException {
		// if (connectionURI == null)
		// connectionURI = getConnectionURI();
		return getConnectionNamedConfig(namedConfig, getConnectionURI(), 5, false, 2);
	}

	/*
	 * public synchronized Connection getConnection(Request request) throws
	 * AmbitException , SQLException{ //if (connectionURI == null) //
	 * connectionURI = getConnectionURI(); return
	 * getConnection(getConnectionURI(request)); }
	 */

	public synchronized Connection getConnectionNamedConfig(String namedConfig, String connectionURI, int maxRetry,
			boolean testIt, int testTimeout) throws AmbitException, SQLException {
		SQLException error = null;
		Connection c = null;

		ResultSet rs = null;
		Statement t = null;
		for (int retry = 0; retry < 5; retry++)
			try {
				error = null;
				DataSource ds = DatasourceFactory.getDataSource(namedConfig, connectionURI,
						loginInfo.getDriverClassName());

				c = ds.getConnection();
				if (testIt) {
					if (c.isValid(testTimeout))
						return c;
					else
						throw new SQLException(String.format("Invalid connection on attempt %d", retry));
				} else
					return c;
			} catch (SQLException x) {
				error = x;
				getLogger().severe(x.getMessage());
				// remove the connection from the pool
				try {
					if (c != null)
						c.close();
				} catch (Exception e) {
				}
			} catch (Throwable x) {
				getLogger().severe(x.getMessage());
				try {
					if (c != null)
						c.close();
				} catch (Exception e) {
				}
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception x) {
				}
				try {
					if (t != null)
						t.close();
				} catch (Exception x) {
				}
			}
		if (error != null)
			throw error;
		else
			throw new SQLException("Can't establish connection " + connectionURI);
	}

	
	protected Object getProperty(String key) {
		return properties.getPropertyWithDefault(key,configFile,null);
		
	}
	
	protected Logger getLogger() {
		return null;
	}

}
