package org.socialmusicdiscovery.server.support.format;

import java.util.Map;

/**
 * Interface which data providers needs to implement, the purpose of a title format is to provide object values to a formatter. For an example of
 * a formatter, see {@link TitleFormat}
 */
public interface DataProvider {
    /**
     * This method is called ones per formatting session to initialize the provider with the objects that might be included in in the formatting string
     *
     * @param objects A map with objects
     */
    public void init(Map<String, Object> objects);

    /**
     * Return the value for the object or attribute with the specified name
     *
     * @param name The name of the object
     * @return The attribute with the specified name
     */
    public Object getValue(String name);
}
