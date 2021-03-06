package net.idea.modbcum.r;

import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.p.DefaultAmbitProcessor;

/**
 * Implementation of {@link Reporter}
 * 
 * @author nina
 * 
 * @param <Content>
 * @param <Output>
 */
public abstract class AbstractReporter<Content, Output> extends DefaultAmbitProcessor<Content, Output> implements
	Reporter<Content, Output> {
    /**
     * 
     */
    private static final long serialVersionUID = 4258695772944173854L;
    /**
	 * 
	 */

    protected Output output;
    protected long timeout = 0;

    protected String licenseURI;

    public String getLicenseURI() {
	return licenseURI;
    }

    public void setLicenseURI(String licenseURI) {
	this.licenseURI = licenseURI;
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

    }

    @Override
    public String getFileExtension() {
	return null;
    }

}
