package net.idea.modbcum.c;

import javax.sql.DataSource;

import net.idea.modbcum.i.IDataSourcePool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceC3P0 implements IDataSourcePool {
    protected volatile ComboPooledDataSource datasource;
    protected boolean verbose = false;

    public boolean isVerbose() {
	return verbose;
    }

    public DataSourceC3P0(String connectURI) throws Exception {
	this(connectURI, "com.mysql.jdbc.Driver");
    }

    public DataSourceC3P0(String connectURI, String driverName) throws Exception {
	this(connectURI, driverName, false);
    }

    public DataSourceC3P0(String connectURI, String driverName, boolean verbose) throws Exception {
	Class.forName(driverName);
	/*
	 * http://www.mchange.com/projects/c3p0/index.html#using_c3p0
	 */
	datasource = new ComboPooledDataSource(); // create a new datasource
						  // object
	datasource.setJdbcUrl(connectURI);
	if (verbose)
	    datasource.setConnectionCustomizerClassName("net.idea.modbcum.c.VerboseConnectionCustomizer");
	datasource.setMaxPoolSize(512);

	/**
	 * http://www.mchange.com/projects/c3p0/index.html#
	 * configuring_connection_testing
	 * 
	 * datasource.setAutomaticTestTable("version");
	 * datasource.setPreferredTestQuery("SELECT 1");
	 * datasource.setTestConnectionOnCheckin(true);
	 * datasource.setIdleConnectionTestPeriod(5);
	 */
	datasource.setTestConnectionOnCheckin(true);
	// datasource.setIdleConnectionTestPeriod(3000);
	datasource.setPreferredTestQuery("/*ping*/ SELECT 1");
	// datasource.setMaxIdleTimeExcessConnections(2400); // should be less
	// than
	// setIdleConnectionTestPeriod
	/**
	 * http://www.mchange.com/projects/c3p0/#checkoutTimeout
	 */
	datasource.setCheckoutTimeout(600000);
	datasource.setNumHelperThreads(10);
    }

    public void close() throws Exception {

	if (datasource != null)
	    datasource.close();

    }

    @Override
    protected void finalize() throws Throwable {
	try {
	    close();
	} catch (Exception x) {

	}
	super.finalize();
    }

    @Override
    public DataSource getDatasource() {
	return datasource;
    }
}
