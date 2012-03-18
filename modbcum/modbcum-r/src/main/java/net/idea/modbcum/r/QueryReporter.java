package net.idea.modbcum.r;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.p.DefaultAmbitProcessor;


/**
 * Executes a query {@link IQueryRetrieval} and reports the results via {@link Reporter} interface
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryReporter<T,Q extends IQueryRetrieval<T>,Output> 
						extends QueryAbstractReporter<T,Q,Output> {

	/**
	 * 
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = -859832577309432246L;

	public QueryReporter() {
		super();
		processors = new ProcessorsChain<T,IBatchStatistics,IProcessor>();
		processors.add(new DefaultAmbitProcessor<T,T>() {
			@Override
			public T process(T target) throws Exception {
				processItem(target);
				return target;
			};
		});
		
	}

	
}
