package net.idea.modbcum.r;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;

public abstract class CliQueryReporter<T, Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 3500571619173311448L;
    protected long started;
    protected long completed;
    protected String comma = null;

    public CliQueryReporter() {
	super();
    }

    @Override
    public Writer process(Q query) throws Exception {
	started = System.currentTimeMillis();
	return super.process(query);
    };

    @Override
    public void header(Writer output, Q query) {

    }

    @Override
    public void footer(Writer output, Q query) {
	completed = System.currentTimeMillis() - started;
    }

    public void footerJSON(Writer writer) throws Exception {
	writer.write(",\n\"retrieved_ms\":");
	writer.write(Long.toString(completed));
	writer.write("\n}\n");
    }

    public long getTimeElapsed() {
	return completed;
    }
}
