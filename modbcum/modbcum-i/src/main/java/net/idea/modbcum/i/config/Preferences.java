/*
Copyright (C) 2005-2007  

Contact: jeliazkova.nina@gmail.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package net.idea.modbcum.i.config;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Preferences {
	protected static PropertyChangeSupport propertyChangeSupport;
	public static String DEFAULT_DIR="defaultDir";
	public static String START_MYSQL="startMySQL";
	public static String MAXRECORDS="MAXRECORDS";
	public static String SCHEME="Scheme";
	public static String DATABASE="Database";
	public static String HOST="Host";
	public static String PORT="Port";
	public static String USER="User";
	public static String PASSWORD="Password";
	public static String TIMEOUT="timeout";
	
	protected final static String filename="ambit2.pref";
	protected static Properties props = null;
	public enum VTAGS { General, Structure,Conversion3D,Database };
	public enum VINDEX { NAME,TITLE,VALUE,CLASS,HINT,HIDDEN,TAG };

	public static Object[][] default_values = {
		//{tag, name, default value, class, hint, hidden,tag}
		{DATABASE,"Default database schema","ambit2",String.class,"This is the default database schema AmbitXT will attempt to connect to when a database connection is required.",false,VTAGS.Database},
		{PORT,"Default database port","3306",String.class,"This is the default port AmbitXT will attempt to connect with when a database connection is required. It is assumed MySQL server runs on this port.",false,VTAGS.Database},
		{USER,"Default user","guest",String.class,"This is the default user name AmbitXT will attempt to connect with when a database connection is required.",false,VTAGS.Database},		
		{HOST,"Host","localhost",String.class,"This is the default host AmbitXT will attempt to connect with when a database connection is required. It is assumed MySQL server runs on this host.",false,VTAGS.Database},
		{SCHEME,"Scheme","jdbc:mysql",String.class,"",true,VTAGS.Database},		
		{START_MYSQL,"Start MySQL automatically","true",Boolean.class,"If checked, the embedded MySQL server will be automatically started upon application launch",false,VTAGS.Database},
		{MAXRECORDS,"Maximum number of records",2000,String.class,"Maximum number of records to be returned by a search query",false,VTAGS.Database},
		{TIMEOUT,"Timeout for search results, ms",60000,String.class,"Timeout of search queries, in milliseconds ",false,VTAGS.Database},
		{DEFAULT_DIR,"Default directory","",String.class,"This folder will appear as a default in the file open or file save dialogs",false,VTAGS.General},		


	};
	
	
	protected static Properties getDefault() {
		Properties p = new Properties();
		for (int i=0; i < default_values.length; i++) {
			p.setProperty(default_values[i][0].toString(),default_values[i][2].toString());			
		}
		return p;
	}
	protected static Properties loadProperties() throws IOException {
		Properties p = new Properties();
		InputStream in = new FileInputStream(new File(filename));
		p.load(in);
		in.close();
		return p;
	}
	public static Properties getProperties() {
		if (props == null)  
			try {
				props = loadProperties();
				if (props.size()==0)
					props = getDefault();
				propertyChangeSupport = new PropertyChangeSupport(props);
			} catch (Exception x) {
				props = getDefault();
				propertyChangeSupport = new PropertyChangeSupport(props);
			}
		return props;
	}

	public static void saveProperties(String comments) throws IOException {
		if (props == null) return;
		OutputStream out = new FileOutputStream(new File(filename));
		props.store(out,comments);
		out.close();
	}
	public static void setProperty(String key, String value) {
		Properties p = getProperties();
		String oldValue = p.getProperty(key);
		p.put(key, value);
		propertyChangeSupport.firePropertyChange(key, oldValue, value);
	}
	public static String getProperty(String key) {
		Properties p = getProperties();
        Object o = p.get(key);
        if (o==null) {
            Properties ps = getDefault();
            o = ps.get(key);
            if (o == null) o = "";
            setProperty(key, o.toString());
        }
        return o.toString();
	}
	public static PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			getProperties();
		return propertyChangeSupport;
	}
	public static void setPropertyChangeSupport(
			PropertyChangeSupport propertyChangeSupport) {
		Preferences.propertyChangeSupport = propertyChangeSupport;
	}	
}


