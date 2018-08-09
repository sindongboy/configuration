package com.skplanet.nlp.config;

import java.io.IOException;
import java.net.URL;

/**
 * Configuration Interface<br>
 *
 * Created by Donghun Shin<br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 5/19/13<br>
 */
public interface Configurable {

    /**
     * Load {@link java.util.Properties} by reading {@code fileName}
     *
     * @param fileName Properties File Name
     */
    public void loadProperties(String fileName) throws IOException;
    public void loadProperties(String fileName, int mode) throws IOException;

    /**
     * Get URL type by reading {@code fileName}
     * @param fileName file path
     * @return URL type for given field {@code fileName}
     */
    public URL getResource(String fileName);
    public URL getResource(String fileName, int mode);

    /**
     * Read {@link java.util.Properties} for given field
     * @param propName property file name
     * @param field field name
     * @return Property value for given field {@code pName}
     */
    public String readProperty(String propName, String field);

    /**
     * Get all the property field names
     * @param propName name of the properties file
     * @return list of the field contained in the properties file
     */
    public String[] propertyNames(String propName);
}
