package net.idea.modbcum.i.bucket;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.idea.modbcum.i.JSONSerializable;
import net.idea.modbcum.i.json.JSONUtils;

public class Bucket<V> implements Serializable, Map<String, V> , JSONSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 586191531888319221L;
	/**
	 * 
	 */

	private Map<String, V> map = new Hashtable<String, V>();
	protected String[] header = {};
	protected int[] columnTypes = {};
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public int[] getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(int[] columnTypes) {
		this.columnTypes = columnTypes;
	}

	public String[] getHeader() {
		return header;
	}

	public void setHeader(String[] header) {
		this.header = header;
	}

	public void toCSV(Writer writer) throws IOException {
		toCSV(writer, "\t");
	}

	public void toCSV(Writer writer, String delimiter) throws IOException {
		for (int i = 0; i < header.length; i++) {
			if (i > 0)
				writer.write(delimiter);
			if (map.get(header[i]) == null)
				continue;
			String c = "";
			if (map.get(header[i]) instanceof String)
				c = "\"";
			writer.write(c);
			Object o = map.get(header[i]);
			if (o instanceof Date) {
				writer.write(sdf.format((Date) o));
			} else if (o instanceof Timestamp) {
				writer.write(sdf.format((Timestamp) o));
			} else if (o instanceof List) {
				continue;
			} else
				writer.write(o.toString());
			writer.write(c);
		}
		writer.flush();
	}

	@Override
	public boolean isEmpty() {
		return map == null ? true : (map.size() == 0);
	}

	@Override
	public int size() {
		return map == null ? 0 : map.size();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public void copy(Bucket<V> object) {
		putAll(object.map);
	}

	@Override
	public boolean containsKey(Object key) {
		return map == null ? false : map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map == null ? false : map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map == null ? null : map.get(key);
	}

	@Override
	public V remove(Object key) {
		if (map == null)
			return null;
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> m) {
		if (map != null)
			map.putAll(m);
	}

	@Override
	public Set<String> keySet() {
		return map == null ? null : map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map == null ? null : map.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		return map == null ? null : map.entrySet();
	}

	@Override
	public V put(String key, V value) {
		if (value != null)
			map.put(key, value);
		return value;
	}

	@Override
	public void clear() {
		if (map != null)
			map.clear();
	}

	public void headerToCSV(Writer writer, String delimiter) throws IOException {
		for (int i = 0; i < header.length; i++) {
			if (i > 0)
				writer.write(delimiter);
			writer.write(header[i]);
		}
		writer.flush();
	}

	public void setHeaders(String[][] headers) {
		int l = 0;
		for (String[] h : headers)
			l += h.length;
		this.header = new String[l];
		l = 0;
		for (String[] h : headers)
			for (String field : h) {
				this.header[l] = field;
				l++;
			}
	}
	@Override
	public String asJSON() {
		StringBuilder writer = new StringBuilder();
		writer.append("\n\t{\n");
		boolean first = true;
		if (getHeader() != null)
			for (int i = 0; i < getHeader().length; i++) {
				String header = getHeader()[i];
				Object o = get(header);

				if (o == null)
					continue;
				if (!first)
					writer.append(",\n");
				first = false;
				writer.append("\t");
				writer.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(header)));
				writer.append(":");

				if (o instanceof String)
					writer.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(o
							.toString())));
				else if (o instanceof Date) {
					writer.append(JSONUtils.jsonQuote(sdf.format((Date) o)));
				} else if (o instanceof Timestamp) {
					writer.append(JSONUtils.jsonQuote(sdf.format((Timestamp) o)));
				} else if (o instanceof Number)
					writer.append(o.toString());
				else if (o instanceof JSONSerializable) {
					writer.append(((JSONSerializable) o).asJSON());
				} else if (o instanceof List) {
					writer.append("[\n");
					String comma = "";
					for (Object result : (List) o) {
						writer.append(comma);
						if (result instanceof JSONSerializable)
							writer.append(((JSONSerializable) result).asJSON());
						else
							writer.append(JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(result.toString())));
						comma = ",\n";
					}
					writer.append("\n]\n");
				} else
					writer.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(o
							.toString())));
			}
		writer.append("\n\t}");
		return writer.toString();
	}
}
