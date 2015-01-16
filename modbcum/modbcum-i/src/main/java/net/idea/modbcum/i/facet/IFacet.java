package net.idea.modbcum.i.facet;

import java.io.Serializable;

public interface IFacet<T> extends Serializable {
    String getTitle();

    String getURL();

    T getValue();

    void setValue(T object);

    int getCount();

    void setCount(int value);

    String getResultsURL(String... params);

    String getSubCategoryURL(String... params);

    String getSubcategoryTitle();

    String toJSON(String uri, String subcategory);
}
