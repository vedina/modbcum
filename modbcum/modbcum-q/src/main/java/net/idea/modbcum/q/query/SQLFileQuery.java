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
		bucket.setHeader(null);
	}

	public SQLFileQuery(String resourceName) throws IOException {
		super();
		sql = loadSQL(resourceName);
		bucket.setHeader(null);
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
			header[i] = md.getColumnLabel(i + 1);
			columnTypes[i] = md.getColumnType(i + 1);
			// java.sql.Types
		}
		bucket.setColumnTypes(columnTypes);
		bucket.setHeader(header);

	}

	@Override
	public Bucket getObject(ResultSet rs) throws AmbitException {

		try {
			if (bucket.getHeader() == null)
				setHeader(rs, bucket);

			bucket.clear();
			for (int i = 0; i < bucket.getHeader().length; i++) {
				String key = bucket.getHeader()[i];

				int type = bucket.getColumnTypes()[i];
				if (type== java.sql.Types.JAVA_OBJECT) continue;
				
				if (rs.getString(key) == null) continue;
				switch (type) {
				case java.sql.Types.TINYINT: {
					bucket.put(key, rs.getByte(key));
					break;
				}
				case java.sql.Types.SMALLINT: {
					bucket.put(key, rs.getShort(key));
					break;
				}
				case java.sql.Types.INTEGER: {
					bucket.put(key, rs.getInt(key));
					break;
				}
				case java.sql.Types.BIGINT: {
					bucket.put(key, rs.getLong(key));
					break;
				}
				case java.sql.Types.REAL: {
					bucket.put(key, rs.getFloat(key));
					break;
				}
				case java.sql.Types.FLOAT: {
					bucket.put(key, rs.getFloat(key));
					break;
				}
				case java.sql.Types.DOUBLE: {
					bucket.put(key, rs.getDouble(key));
					break;
				}
				case java.sql.Types.DATE: {
					bucket.put(key, rs.getDate(key));
					break;
				}
				case java.sql.Types.TIME: {
					bucket.put(key, rs.getTime(key));
					break;
				}
				case java.sql.Types.TIMESTAMP: {
					bucket.put(key, rs.getTimestamp(key));
					break;
				}
				case java.sql.Types.DECIMAL: {
					bucket.put(key, rs.getBigDecimal(key));
					break;
				}
				case java.sql.Types.CHAR: {
					bucket.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.VARCHAR: {
					bucket.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.LONGNVARCHAR: {
					bucket.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.LONGVARCHAR: {
					bucket.put(key, rs.getString(key));
					break;
				}
				case java.sql.Types.CLOB: {
					bucket.put(key, rs.getString(key));
					break;
				}

				case java.sql.Types.NUMERIC: {
					bucket.put(key, rs.getDouble(key));
					break;
				}
				case java.sql.Types.JAVA_OBJECT: {
					break;
				}
				case java.sql.Types.VARBINARY : {
					bucket.put(key, rs.getString(key));
					break;
				}
				default: {
					//System.out.println(type);
				}
				}

			}
			return bucket;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

}