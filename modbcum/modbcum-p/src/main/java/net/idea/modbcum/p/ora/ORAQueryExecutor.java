package net.idea.modbcum.p.ora;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.p.ProcessorException;
import net.idea.modbcum.p.QueryExecutor;

/**
 * 
 * {@link QueryExecutor} descendant taking care of Oracle specifics
 * @param <Q>
 */
public class ORAQueryExecutor<Q extends IQueryObject> extends QueryExecutor<Q> {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1378224376428350114L;
	public ORAQueryExecutor() {
		super();
		setCache(true);
		setResultTypeConcurency(ResultSet.CONCUR_READ_ONLY);
		setResultType(ResultSet.TYPE_FORWARD_ONLY);
	}
	public String getSQL(Q target) throws net.idea.modbcum.i.exceptions.AmbitException {
		//ORACLE doesn't support  MySQL LIMIT syntax, so we skip the paginations for now
		return target.getSQL();
	};
	@Override
	public synchronized ResultSet process(Q target) throws Exception {

		long now = System.currentTimeMillis();
		Connection c = getConnection();		
		if (c == null) throw new AmbitException("no connection");

		ResultSet rs = null;
		try {
				List<QueryParam> params = target.getParameters();
				if (params == null) {
					statement = c.createStatement(getResultType(),getResultTypeConcurency());
					String sql = getSQL(target);
					
					rs = statement.executeQuery(sql);

				} else {
					String sql = getSQL(target);
					sresults = getCachedStatement(sql);
					
					if (sresults == null) {
						sresults = c.prepareStatement(sql,getResultType(),getResultTypeConcurency());
						if (isCache())	addStatementToCache(sql,sresults);		
					} else {
						sresults.clearParameters();
					}					

					QueryExecutor.setParameters(sresults, params);
					logger.debug(sresults);
					sresults.setFetchDirection(ResultSet.FETCH_FORWARD);
					//errors in oracle
					//sresults.setFetchSize(1);
					//4 * 1024 * 1024 / sum(data_length for all columns of your table) is a reasonable fetch size.
					//http://www.oracle.com/technetwork/database/enterprise-edition/memory.pdf
					sresults.setFetchSize(100);
					rs = sresults.executeQuery();

				}
		} catch (Exception x) {
			try {System.err.println(x.getMessage() + " " +sresults); } catch (Exception xx) {}
			throw new ProcessorException(this,x);
		} catch (Throwable x) { 
			throw new ProcessorException(this,x.getMessage());
		}
		finally {

		}
		return rs;
	}
}
