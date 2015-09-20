package net.idea.modbcum.q.query;

import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;

public abstract class BucketQuery<V> extends AbstractQuery<Bucket,V,EQCondition,Bucket> implements IQueryRetrieval<Bucket> {
	protected Bucket placeHolder = new Bucket();
	/**
	 * 
	 */
	private static final long serialVersionUID = -8767675520165955438L;
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(Bucket object) {
		return 1;
	}
}
