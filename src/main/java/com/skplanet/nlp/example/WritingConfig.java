package com.skplanet.nlp.example;


import com.skplanet.nlp.writer.ConfigWriter;

/**
 * Shows How to Create properties file using {@link ConfigWriter}
 * <br>Created by Donghun Shin<br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 6/18/13<br>
 */
public class WritingConfig {
    public static void main(String[] args) {
        // Get the System Context
        String classpath = "/Users/sindongboy/Documents/workspace/OMPConfiguration/config";

        // Get Writer instance.
        ConfigWriter writer = ConfigWriter.getInstance();
        // Set Base Directory. later, this path must be added to your classpath.
        writer.setBaseDir(classpath);

        // Open (Create) properties file
        writer.open("sample.properties");

        // [optional] write header (string starts starts with "#" )
        writer.addHeader("sample.properties", "sample header line 1\nsample header line 2");

        // add a field and value
        writer.addSingleConfig("sample.properties", "field1", "value1");

        // add multiple fields and values
        String[] fields = new String[]{"field2", "field3", "field4"};
        String[] values = new String[]{"value2", "value3", "value4"};
        writer.addMultipleConfig("sample.properties", fields, values);

        writer.close("sample.properties");
        // or
        //writer.closeAll();

    }
}
