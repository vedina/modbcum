package net.idea.modbcum.i;

import java.sql.ResultSet;

import net.idea.modbcum.i.exceptions.AmbitException;

public interface IRetrieval<V> {
	V getObject(ResultSet rs) throws AmbitException;
}
