package net.idea.modbcum.r;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.BatchProcessingException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.p.AbstractDBProcessor;
import net.idea.modbcum.p.DbReader;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

public abstract class QueryAbstractReporter<T, Q extends IQueryRetrieval<T>, Output> extends
	AbstractDBProcessor<Q, Output> implements Reporter<Q, Output> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5670983340220442580L;
    protected Output output = null;
    protected int maxRecords = 0;
    protected boolean showHeader = true;
    protected boolean showFooter = true;
    protected boolean autoCommit = false;
    protected long timeout = 0;
    protected AbstractBatchProcessor batch;
    protected String licenseURI;
    protected long startTime;

    public long getStartTime() {
	return startTime;
    }

    public void setStartTime(long startTime) {
	this.startTime = startTime;
    }

    public String getLicenseURI() {
	return licenseURI;
    }

    public void setLicenseURI(String licenseURI) {
	this.licenseURI = licenseURI;
    }

    public abstract void header(Output output, Q query);

    public abstract void footer(Output output, Q query);

    protected ProcessorsChain<T, IBatchStatistics, IProcessor> processors;

    public ProcessorsChain<T, IBatchStatistics, IProcessor> getProcessors() {
	return processors;
    }

    public void setProcessors(ProcessorsChain<T, IBatchStatistics, IProcessor> processors) {
	this.processors = processors;
    }

    public abstract Object processItem(T item) throws Exception;

    public boolean isAutoCommit() {
	return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
	this.autoCommit = autoCommit;
    }

    public boolean isShowHeader() {
	return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
	this.showHeader = showHeader;
    }

    public boolean isShowFooter() {
	return showFooter;
    }

    public void setShowFooter(boolean showFooter) {
	this.showFooter = showFooter;
    }

    public int getMaxRecords() {
	return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
	this.maxRecords = maxRecords;
    }

    public Output getOutput() throws Exception {
	return output;
    }

    public void setOutput(Output output) throws Exception {
	this.output = output;
    }

    public long getTimeout() {
	return timeout;
    }

    public void setTimeout(long timeout) {
	this.timeout = timeout;

	if (batch != null)
	    batch.setTimeout(timeout);
    }

    @Override
    public void setCloseConnection(boolean closeConnection) {
	super.setCloseConnection(closeConnection);
	if (batch != null)
	    batch.setCloseConnection(closeConnection);
    }

    public Output process(Q query) throws Exception {
	output = getOutput();
	if (isShowHeader())
	    header(output, query);

	batch = createBatch(query);
	batch.setCloseConnection(closeConnection);
	batch.setTimeout(getTimeout());
	IBatchStatistics stats = null;
	try {
	    if (connection != null) {
		batch.setProcessorChain(processors);
		batch.setConnection(connection);
		stats = batch.process(query);
		if (stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ) == 0)
		    throw new NotFoundException(query.toString());
		wrapup();
	    }
	    return output;

	} catch (Exception x) {
	    // TODO smth reasonable for java.io.IOException: An established
	    // connection was aborted by the software in your host machine
	    try {
		if (isAutoCommit())
		    connection.rollback();
	    } catch (Exception xx) {
	    }
	    if (stats != null)
		throw new BatchProcessingException(x, batch);
	    else
		throw x;

	} finally {
	    if (isShowFooter())
		footer(output, query);
	    try {
		if (isAutoCommit())
		    connection.commit();
	    } catch (Exception x) {
	    }
	    try {
		batch.close();
	    } catch (Exception x) {
	    }
	    try {
		close();
	    } catch (Exception x) {
	    }
	}
    }

    protected AbstractBatchProcessor<IQueryRetrieval<T>, T> createBatch(Q query) {
	DbReader<T> reader = new DbReader<T>();
	reader.setHandlePrescreen(true);
	return reader;
    }

    protected void wrapup() throws AmbitException {

    }

    @Override
    public String getFileExtension() {
	return null;
    }
}
