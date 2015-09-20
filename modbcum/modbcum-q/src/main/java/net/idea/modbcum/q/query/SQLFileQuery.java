package net.idea.modbcum.q.query;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;

public class SQLFileQuery<V> extends BucketQuery<V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4157440585791922619L;
	protected final String sql;

	public SQLFileQuery(File sqlFile) throws IOException {
		super();
		sql = loadSQL(sqlFile);
		placeHolder.setHeader(null);
	}

	public SQLFileQuery(String resourceName) throws IOException {
		super();
		sql = loadSQL(resourceName);
		placeHolder.setHeader(null);
	}

	protected String loadSQL(String resourceName) throws IOException {
		InputStream stream = null;
		try {
			stream = this.getClass().getClassLoader()
					.getResourceAsStream(resourceName);
			if (stream == null)
				throw new IOException("Error loading " + resourceName);
			return loadSQL(stream);
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				stream.close();
			} catch (Exception x) {
			}
		}
	}

	protected String loadSQL(InputStream stream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] bytes = new byte[512];
			int len;
			long count = 0;
			while ((len = stream.read(bytes, 0, bytes.length)) != -1) {
				out.write(bytes, 0, len);
				count += len;
			}
			out.flush();

			return out.toString();
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				out.close();
			} catch (Exception x) {
			}
		}
	}

	protected String loadSQL(File sqlFile) throws IOException {
		InputStream stream = null;
		try {
			stream = new FileInputStream(sqlFile);
			return loadSQL(stream);
		} catch (IOException x) {
			throw x;
		} finally {
			try {
				stream.close();
			} catch (Exception x) {
			}
		}
	}

	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	protected void setHeader(ResultSet rs, Bucket bucket) throws SQLException {

		ResultSetMetaData md = rs.getMetaData();
		String[] header = new String[md.getColumnCount()];
		int[] columnTypes = new int[md.getColumnCount()];
		for (int i = 0; i < md.getColumnCount(); i++) {
			header[i] = md.getColumnName(i + 1);
			if (header[i].toUpperCase().indexOf("OPS") == 0)
				header[i] = header[i].toUpperCase(); // hack
			columnTypes[i] = md.getColumnType(i + 1);
			if ((java.sql.Types.NUMERIC == columnTypes[i])
					&& (header[i].toUpperCase().indexOf("_ID") > 0)) {
				columnTypes[i] = java.sql.Types.BIGINT;
			}

			// java.sql.Types
		}
		bucket.setColumnTypes(columnTypes);
		bucket.setHeader(header);

	}

	@Override
	public Bucket getObject(ResultSet rs) throws AmbitException {

		try {
			if (placeHolder.getHeader() == null)
				setHeader(rs, placeHolder);

			placeHolder.clear();
			for (int i = 0; i < placeHolder.getHeader().length; i++) {
				String key = placeHolder.getHeader()[i];
				if (key.toUpperCase().indexOf("OPS") == 0)
					key = key.toUpperCase(); // hack
				int type = placeHolder.getColumnTypes()[i];
				switch (type) {
				case java.sql.Types.TINYINT: {
					placeHolder.put(key, rs.getByte(key));
					break;
				}
				case java.sql.Types.SMALLINT: {
					placeHolder.put(key, rs.getShort(key));
					break;
				}
				case java.sql.Types.INTEGER: {
					placeHolder.put(key, rs.getInt(key));
					break;
				}
				case java.sql.Types.BIGINT: {
					placeHolder.put(key, rs.getLong(key));
					break;
				}
				case java.sql.Types.REAL: {
					placeHolder.put(key, rs.getFloat(key));
					break;
				}
				case java.sql.Types.FLOAT: {
					placeHolder.put(key, rs.getFloat(key));
					break;
				}
				case java.sql.Types.DOUBLE: {
					placeHolder.put(key, rs.getDouble(key));
					break;
				}
				case java.sql.Types.DATE: {
					placeHolder.put(key, rs.getDate(key));
					break;
				}
				case java.sql.Types.TIME: {
					placeHolder.put(key, rs.getTime(key));
					break;
				}
				case java.sql.Types.TIMESTAMP: {
					placeHolder.put(key, rs.getTimestamp(key));
					break;
				}
				case java.sql.Types.DECIMAL: {
					placeHolder.put(key, rs.getBigDecimal(key));
					break;
				}
				case java.sql.Types.CHAR: {
					placeHolder.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.VARCHAR: {
					placeHolder.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.LONGNVARCHAR: {
					placeHolder.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.CLOB: {
					placeHolder.put(key, rs.getString(key));
					break;
				}

				case java.sql.Types.NUMERIC: {
					placeHolder.put(key, rs.getDouble(key));
					break;
				}

				}

			}
			return placeHolder;
		} catch (Exception x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
	}

}