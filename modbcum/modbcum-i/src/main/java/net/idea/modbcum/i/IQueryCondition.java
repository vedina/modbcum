package net.idea.modbcum.i;

import java.io.Serializable;



public interface IQueryCondition extends Serializable {
	String getName();
	String getSQL();
}
