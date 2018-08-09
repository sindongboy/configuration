package com.skplanet.nlp.writer;

/**
 * Configuration Writer Interface<br>
 *
 * <br>Created by Donghun Shin<br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 5/19/13<br>
 */
public interface ConfigWritable {

    /**
     * write configuration for given configuration file
     * @param fileName Configuration file
     */
    public void write(String fileName);

    /**
     * Set directory for saving configuration files
     * @param dir directory for saving configuration files
     */
    public void setBaseDir(String dir);

    /**
     * Open the given configuration file for writing
     * @param fileName configuration file
     * @return {@link String} : the canonical path for the configuration file
     */
    public String open(String fileName);

    /**
     * Close the given configuration file
     * @param fileName configuration file
     * @return {@link String} : the canonical path for the configuration file
     */
    public String close(String fileName);

    /**
     * Delete the configuration file
     * @param fileName configuration file
     * @return {@link String} : the canonical path for the configuration file
     */
    public String delete(String fileName);

    /**
     * Class all the configuration files
     */
    public void closeAll();

    /**
     * Add configuraton item
     * @param fileName configuration file
     * @param field field
     * @param value value
     */
    public void addSingleConfig(String fileName, String field, String value);

    /**
     * add configuration items
     * @param fileName configuration file
     * @param fields fields
     * @param values values
     */
    public void addMultipleConfig(String fileName, String[] fields, String[] values);

}
