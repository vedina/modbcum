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

    public DataSourceC3P0(String namedConfig, String connectURI) throws Exception {
	this(namedConfig, connectURI, "com.mysql.jdbc.Driver");
    }

    public DataSourceC3P0(String namedConfig, String connectURI, String driverName) throws Exception {
	this(namedConfig, connectURI, driverName, false);
    }

    public DataSourceC3P0(String namedConfig, String connectURI, String driverName, boolean verbose) throws Exception {
	Class.forName(driverName);
	/*
	 * http://www.mchange.com/projects/c3p0/index.html#using_c3p0
	 */
	if (namedConfig != null)
	    datasource = new ComboPooledDataSource(namedConfig);
	else
	    datasource = new ComboPooledDataSource();
	// object
	datasource.setJdbcUrl(connectURI);

	if (verbose)
	    datasource.setConnectionCustomizerClassName("net.idea.modbcum.c.VerboseConnectionCustomizer");
	    /**
	     * http://www.mchange.com/projects/c3p0/index.html#
	     * configuring_connection_testing
	*/

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
