/* DatasourceFactory.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: jeliazkova.nina@gmail.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package net.idea.modbcum.c;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import net.idea.modbcum.i.IDataSourcePool;
import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.exceptions.AmbitException;

public class DatasourceFactory {
    protected static final String slash = "/";
    protected static final String qmark = "?";
    protected static final String colon = ":";
    protected static final String eqmark = "=";
    protected static final String amark = "&";
    protected ConcurrentHashMap<String, IDataSourcePool> datasources;

    private DatasourceFactory() {
	datasources = new ConcurrentHashMap<String, IDataSourcePool>();
    }

    private static class DatasourceFactoryHolder {
	private final static DatasourceFactory instance = new DatasourceFactory();
    }

    public static synchronized DatasourceFactory getInstance() {
	return DatasourceFactoryHolder.instance;
    }

    public static synchronized DataSource getDataSource(String namedConfig,String connectURI, String driverName) throws Exception {
	if (connectURI == null)
	    throw new AmbitException("Connection URI not specified!");

	IDataSourcePool ds = getInstance().datasources.get(connectURI);
	if (ds == null) {
	    ds = setupDataSource(namedConfig,connectURI, driverName);
	    IDataSourcePool oldds = getInstance().datasources.putIfAbsent(connectURI, ds);
	    if (oldds != null)
		ds = oldds;

	}
	if (ds != null)
	    return ds.getDatasource();
	else
	    return null;

    }

    /**
     * Does nothing, the pool will be still active
     * 
     * @param connectURI
     * @throws AmbitException
     */
    public static void logout(String connectURI) throws AmbitException {

	/*
	 * 
	 * if (connectURI == null) throw new
	 * AmbitException("Connection URI not specified!"); DataSourceAndPool
	 * dspool = getInstance().datasources.get(connectURI); if (dspool !=
	 * null) { getInstance().datasources.remove(connectURI); try {
	 * 
	 * } catch (Exception x) {x.printStackTrace();}; }
	 */

    }

    public static Connection getConnection(String namedConfig,String connectURI, String driverName) throws Exception {
	try {
	    Connection connection = getDataSource(namedConfig,connectURI, driverName).getConnection();
	    if (connection.isClosed())
		return getDataSource(namedConfig,connectURI, driverName).getConnection();
	    else
		return connection;
	} catch (Exception x) {
	    // do smth
	    throw x;
	}
    }

    public static synchronized IDataSourcePool setupDataSource(String namedConfig,String connectURI, String driverName) throws Exception {
	try {
	    // IDataSourcePool dataSource = new DataSourceAndPool(connectURI);
	    // IDataSourcePool dataSource = new DataSourceBoneCP(connectURI);
	    IDataSourcePool dataSource = new DataSourceC3P0(namedConfig,connectURI, driverName);

	    return dataSource;
	} catch (Exception x) {
	    throw x;
	}
    }

    /**
     * Assembles connection URI
     * 
     * @param scheme
     * @param hostname
     * @param port
     * @param database
     * @param user
     * @param password
     * @return 
     *         scheme://{Hostname}:{Port}/{Database}?user={user}&password={password
     *         }
     */
    public static String getConnectionURI(String scheme, String hostname, String port, String database, String user,
	    String password) {

	StringBuilder b = new StringBuilder();
	b.append(scheme).append(colon).append(slash).append(slash);

	if (hostname == null)
	    b.append("localhost");
	else
	    b.append(hostname);
	if (port != null)
	    b.append(colon).append(port);
	b.append(slash).append(database);
	String q = qmark;
	if (user != null) {
	    b.append(q);
	    q = amark;
	    b.append("user").append(eqmark).append(user);
	}
	if (password != null) {
	    b.append(q);
	    q = amark;
	    b.append("password").append(eqmark).append(password);
	}
	b.append(amark);
	b.append("&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&noAccessToProcedureBodies=true&useSSL=false");
	// useUsageAdvisor=true");
	// profileSQL=true");
	// &dontTrackOpenResources=true

	// b.append("[validationQuery]=[SELECT 1]");
	return b.toString();
    }

    /**
     * 
     * @param scheme
     * @param hostname
     * @param port
     * @param database
     * @return scheme://{Hostname}:{Port}/{Database}
     */
    public static String getConnectionURI(String scheme, String hostname, String port, String database) {
	return getConnectionURI(scheme, hostname, port, database, null, null);
    }

    public static String getConnectionURI(String hostname, String port, String database) {
	return getConnectionURI("jdbc:mysql", hostname, port, database, null, null);
    }

}
