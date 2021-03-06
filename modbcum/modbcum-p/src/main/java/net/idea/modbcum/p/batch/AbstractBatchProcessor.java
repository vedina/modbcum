/* DBReader.java
 * Author: nina
 * Date: Feb 8, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package net.idea.modbcum.p.batch;

import java.sql.Connection;
import java.util.Iterator;
import java.util.logging.Level;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.batch.DefaultBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IBatchProcessor;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.AbstractDBProcessor;

/**
 * 
 * @author nina
 * 
 */
public abstract class AbstractBatchProcessor<Target, ItemInput> extends
		AbstractDBProcessor<Target, IBatchStatistics> implements
		IBatchProcessor<Target, ItemInput, IBatchStatistics> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6221299717393378599L;
	protected IBatchStatistics batchStatistics = null;
	public static String PROPERTY_BATCHSTATS = "ambit2.core.processors.batch.IBatchStatistics";
	protected long now = System.currentTimeMillis(); // ms
	protected ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> processor;
	protected long timeout = 0;
	protected boolean cancelled = false;

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public synchronized void cancel() {
		this.cancelled = true;
	}

	public AbstractBatchProcessor() {
		// TODO Auto-generated constructor stub
	}

	public AbstractBatchProcessor(
			ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> processor) {
		super();
		setProcessorChain(processor);
	}

	public ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> getProcessorChain() {
		return processor;
	}

	public void setProcessorChain(
			ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> processor) {
		this.processor = processor;

	}

	public IBatchStatistics process(Target target) throws Exception {
		long started = System.currentTimeMillis();
		beforeProcessing(target);
		IBatchStatistics result = getResult(target);
		ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> processor = getProcessorChain();
		Iterator<ItemInput> i = getIterator(target);
		logger.log(Level.FINE, "Processing started");
		cancelled = false;
		while (i.hasNext() && !cancelled) {

			ItemInput input = null;

			try {
				input = i.next();
				onItemRead(input, result);
				if (skip(input,result)) {
					onItemSkipped(input, result);
					continue;
				}
			} catch (Exception x) {
				onError(input, null, result, x);
				continue;
			}

			try {
				if (processor != null) {
					Object output = processor.process(input);
					try {
						onItemProcessed(input, output, result);
					} catch (ClassCastException x) {
						// weird class cast exception
						onItemProcessed(null, output, result);
					}
				}
			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage(), x);
				onError(input, null, result, x);
				continue;
			}
			long elapsed = System.currentTimeMillis() - started;
			if ((timeout > 0) && (elapsed) > timeout) {
				onError(input,
						null,
						result,
						new AmbitException(String.format(
								"Aborted on timeout %d", elapsed)));
				break;
			}
		}

		afterProcessing(target, i);
		return result;
	}

	public void afterProcessing(Target target,
			java.util.Iterator<ItemInput> iterator) throws AmbitException {
		propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS, null,
				batchStatistics);
	}

	public void beforeProcessing(Target target) throws AmbitException {
		now = System.currentTimeMillis();
		batchStatistics = getResult(target);

	}

	public IBatchStatistics getResult(Target target) {
		if (batchStatistics != null)
			return batchStatistics;
		batchStatistics = new DefaultBatchStatistics();
		batchStatistics.setResultCaption("Read");
		long freq = 1;
		((DefaultBatchStatistics) batchStatistics).setFrequency(freq);
		return batchStatistics;
	}

	public void onItemProcessing(ItemInput input, Object output,
			IBatchStatistics stats) {
		long freq = stats.getFrequency();
		if ((stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ) % freq) == 0)
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS, null,
					stats);

	}

	@Override
	public void open() throws Exception {
		if (getProcessorChain() != null)
			for (IProcessor p : getProcessorChain())
				if (p instanceof IDBProcessor)
					try {
						((IDBProcessor) p).open();
					} catch (Exception x) {
					}

	}

	public void onError(ItemInput input, Object output, IBatchStatistics stats,
			Exception x) {
		stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR);
		stats.incrementTimeElapsed(
				IBatchStatistics.RECORDS_STATS.RECORDS_ERROR,
				System.currentTimeMillis() - now);
		if (stats.isTimeToPrint(getSilentInterval()))
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS, null,
					stats);		
		now = System.currentTimeMillis();
	}

	public void onItemProcessed(ItemInput input, Object output,
			IBatchStatistics stats) {
		stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED);
		stats.incrementTimeElapsed(
				IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED,
				System.currentTimeMillis() - now);
		if (stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED)==1 || stats.isTimeToPrint(getSilentInterval()))
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS, null,
					stats);
		now = System.currentTimeMillis();

	}

	public long getSilentInterval() {
		return 500;
	}
	public boolean skip(ItemInput input, IBatchStatistics stats) {
		return false;
	}
	
	public void onItemSkipped(ItemInput input, 	IBatchStatistics stats) {
		stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_SKIPPED);
		stats.incrementTimeElapsed(
				IBatchStatistics.RECORDS_STATS.RECORDS_SKIPPED,
				System.currentTimeMillis() - now);
		now = System.currentTimeMillis();

	}
	
	public void onItemRead(ItemInput input, IBatchStatistics stats) {
		stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_READ);
		stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_READ,
				System.currentTimeMillis() - now);
		now = System.currentTimeMillis();

	}

	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		if (getProcessorChain() != null)
			for (IProcessor p : getProcessorChain())
				if (p instanceof IDBProcessor)
					((IDBProcessor) p).setConnection(connection);

	}

	@Override
	public void close() throws Exception {
		if (!closeConnection) {
			try {
				setConnection(null);
			} catch (Exception x) {
			}
		} else {
			if (processor != null)
				for (IProcessor p : getProcessorChain())
					if (p instanceof IDBProcessor)
						try {
							((IDBProcessor) p).close();
						} catch (Exception x) {
						}
			super.close();
		}
	}
}
