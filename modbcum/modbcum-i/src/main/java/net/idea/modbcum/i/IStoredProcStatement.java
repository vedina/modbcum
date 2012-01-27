package net.idea.modbcum.i;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface IStoredProcStatement {
	public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException;
}
