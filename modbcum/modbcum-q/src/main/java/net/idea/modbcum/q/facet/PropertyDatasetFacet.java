package net.idea.modbcum.q.facet;

/**
 * 
 * @author nina
 * 
 * @param <PROPERTY>
 * @param <DATASET>
 */
public class PropertyDatasetFacet<PROPERTY1, PROPERTY2> extends AbstractFacet<String> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -2260081381044742166L;
    protected PROPERTY1 property;
    protected PROPERTY2 dataset;

    public PropertyDatasetFacet(String url) {
	super(url);
    }

    public PROPERTY1 getProperty1() {
	return property;
    }

    public void setProperty1(PROPERTY1 property) {
	this.property = property;
    }

    public PROPERTY2 getProperty2() {
	return dataset;
    }

    public void setProperty2(PROPERTY2 dataset) {
	this.dataset = dataset;
    }

    @Override
    public String toString() {
	return String.format("%s (%d)", getValue(), getCount());
    }

}