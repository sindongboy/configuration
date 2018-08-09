package com.skplanet.nlp.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Configuration Item Container
 *
 * Created by Donghun Shin
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * Date: 5/19/13
 */
public class ConfigItem {


    /**
     * Configuration field and value pair
     */
    private HashMap<String, String> items = null;

    /**
     * Tag for Configuration Header
     */
    public static final String HEADER_TAG = "# ";

    /**
     * Constructor
     */
    public ConfigItem() {
        this.items = new HashMap<String, String>();
    }

    /**
     * Get the size of field and value pair, or Configuration item
     * @return
     */
    public int size() {
        return this.items.size();
    }

    /**
     * Add Configuration item
     * @param field field
     * @param value value
     */
    public void addOneItem(String field, String value) {
        if (!this.items.containsKey(field)) {
            this.items.remove(field);
            this.items.put(field, value);
        } else {
            this.items.put(field, value);
        }
    }

    /**
     * Add Configuration items
     * @param fields fields
     * @param values values
     */
    public void addAllItem(String[] fields, String [] values) {
        int length = fields.length;
        if (fields.length != values.length) {
            System.err.println("[ERROR] fields and values are not paired");
            return;
        }

        for (int i = 0; i < length; i++) {
            this.addOneItem(fields[i], values[i]);
        }
    }

    @Override
    public String toString() {
        ArrayList<String> config = new ArrayList<String>();
        Set<String> keys = this.items.keySet();
        StringBuilder out = new StringBuilder();

        for (String key : keys) {
            out.append(key + "=" + this.items.get(key) + "\n");
        }
        return out.toString();
    }

    /**
     * Sample Program
     * @param args no args needed
     */
    public static void main(String[] args) {
        ConfigItem ci = new ConfigItem();

        ci.addOneItem("dir", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");
        ci.addOneItem("dir2", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");

        System.out.println(ci.toString());
    }

}
