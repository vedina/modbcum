package net.idea.modbcum.i.batch;

import java.util.Observable;

/**
 * 
 * Default implementation of {@link IBatchStatistics}
 * 
 * @author Nina Jeliazkova jeliazkova.nina@gmail.com <b>Modified</b> Aug 29,
 *         2006
 */
public class DefaultBatchStatistics extends Observable implements
		IBatchStatistics {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8714835013929249115L;
	protected long[] records;
	protected long[] time_elapsed;
	protected long frequency = 50;
	protected long last_time_print_stats = 0;

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	protected static final String blank = "";
	protected boolean inProgress = true;
	protected String resultCaption = "Found";

	public DefaultBatchStatistics() {
		super();
		records = new long[IBatchStatistics.RECORDS_STATS.values().length];
		time_elapsed = new long[IBatchStatistics.RECORDS_STATS.values().length];
		for (int i = 0; i < records.length; i++) {
			records[i] = 0;
			time_elapsed[i] = 0;
		}
	}

	public void clear() {
		for (int i = 0; i < records.length; i++) {
			records[i] = 0;
			time_elapsed[i] = 0;
		}
		inProgress = true;
		setChanged();
		notifyObservers();

	}

	public void completed() {
		inProgress = false;
		setChanged();
		notifyObservers();

	}

	public long getRecords(IBatchStatistics.RECORDS_STATS recordType) {
		return records[recordType.ordinal()];
	}

	public void setRecords(IBatchStatistics.RECORDS_STATS recordType,
			long number) {
		records[recordType.ordinal()] = number;
		setChanged();
		notifyObservers();
	}

	public void increment(IBatchStatistics.RECORDS_STATS recordType) {
		records[recordType.ordinal()]++;
		setChanged();
		notifyObservers();
	}

	public long getTimeElapsed() {
		long sum = 0;
		for (int i = 0; i < time_elapsed.length; i++)
			sum += time_elapsed[i];
		/*
		 * return time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_READ
		 * .ordinal()] +
		 * time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED
		 * .ordinal()] +
		 * time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_ERROR .ordinal()]
		 * ;
		 */
		return sum;
	}

	public long getTimeElapsed(RECORDS_STATS recordType) {
		return time_elapsed[recordType.ordinal()];
	}

	public void setTimeElapsed(RECORDS_STATS recordType, long milliseconds) {
		time_elapsed[recordType.ordinal()] = milliseconds;
		setChanged();
		notifyObservers();
	}

	public void incrementTimeElapsed(RECORDS_STATS recordType, long milliseconds) {
		time_elapsed[recordType.ordinal()] += milliseconds;
		setChanged();
		notifyObservers();
	}

	public boolean isTimeToPrint(long silentInterval) {
		// (getRecords(RECORDS_STATS.RECORDS_READ) % frequency) == 0)

		long elapsed = System.currentTimeMillis() - last_time_print_stats;
		if ((elapsed) >= silentInterval) {
			last_time_print_stats = System.currentTimeMillis();
			return true;
		} else
			return false;
	}

	public String toString() {
		long t = getTimeElapsed();

		StringBuilder b = new StringBuilder();
		b.append("Records ");
		for (RECORDS_STATS stats : RECORDS_STATS.values()) {
			long records = getRecords(stats);
			b.append(stats.toString());
			b.append(":");
			b.append(Long.toString(records));
			if (records > 0) {
				b.append(' ');
				double s = ((double) time_elapsed[stats.ordinal()]) / ((double) records);
				b.append(String.format(" [%s msec, %-10.3f msec/record]\t", time_elapsed[stats.ordinal()],s));
			} else b.append('\t');
		}
		/*
		 * if (getRecords(RECORDS_STATS.RECORDS_READ) > 0) { double s =
		 * ((double) t) / ((double) getRecords(RECORDS_STATS.RECORDS_READ));
		 * b.append(String.format("(%f msec per record)", s)); }
		 */
		return b.toString();
	}

	public String getResultCaption() {
		return resultCaption;
	}

	public void setResultCaption(String resultCaption) {
		this.resultCaption = resultCaption;
	}
}
