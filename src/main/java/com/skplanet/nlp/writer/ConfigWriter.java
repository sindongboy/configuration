package com.skplanet.nlp.writer;


import com.skplanet.nlp.config.ConfigItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Simple Configuration Writer<br>
 *
 * <br>Created by Donghun Shin<br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 5/19/13<br>
 */
public class ConfigWriter implements ConfigWritable{

    // Instance
    private static ConfigWriter instance = null;

    // Control Configuration files
    private HashMap<String, BufferedWriter> configList = null;

    // Control Configuration contents
    private HashMap<String, ConfigItem> configItem = null;

    // Control Configuration headers
    private HashMap<String, String> header = null;

    // classpath for saving configuration files
    private File BASE_DIR = null;


    /**
     * Get an instance of {@link ConfigWriter}
     *
     * @return an instance of {@link ConfigWriter}
     */
    public static ConfigWriter getInstance() {
        if (instance == null) {
            synchronized (ConfigWriter.class) {
                instance = new ConfigWriter();
            }
        }
        return instance;
    }

    /**
     * Constructor : just prevent any other classes from instantiating
     */
    private ConfigWriter() {
        this.configList = new HashMap<String, BufferedWriter>();
        this.configItem = new HashMap<String, ConfigItem>();
        this.header = new HashMap<String, String>();
    }

    /**
     * Set Header for given configuration
     * @param fileName configuration file name
     * @param header contents of header
     */
    public void addHeader(String fileName, String header) {

        File dFile = new File(BASE_DIR + "/" + fileName);
        try {
            if (this.header.containsKey(dFile.getCanonicalPath())) {
                this.header.remove(dFile.getCanonicalPath());
                this.header.put(dFile.getCanonicalPath(), header);
            } else {
                this.header.put(dFile.getCanonicalPath(), header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear all the properties for given configuration file
     * @param fileName configuration file
     */
    public void clear(String fileName) {
        File dFile = new File(BASE_DIR + "/" + fileName);
        try {
            if (this.configList.containsKey(dFile.getCanonicalPath())) {
                this.close(fileName);
                this.open(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String fileName) {
        File dFile = new File(BASE_DIR + "/" + fileName);
        BufferedWriter writer;
        String []lines;
        try {
            writer = this.configList.get(dFile.getCanonicalPath());
            ConfigItem cItem = this.configItem.get(dFile.getCanonicalPath());
            if (this.header.containsKey(dFile.getCanonicalPath())) {
                lines = this.header.get(dFile.getCanonicalPath()).split("\n");
                for (String line : lines) {
                    writer.write(ConfigItem.HEADER_TAG + line + "\n");
                }
            }
            writer.write(cItem.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBaseDir(String dir) {
        this.BASE_DIR = new File(dir);
        if (!BASE_DIR.exists()) {
            System.err.println("[ERROR] Directory doesn't exist: " + dir);
        }

        if (!BASE_DIR.isDirectory()) {
            System.err.println("[ERROR] needs directory, not file: " + dir);
        }
    }

    @Override
    public String open(String fileName) {
        if (BASE_DIR == null) {
            System.err.println("[ERROR] Base Directory must be set before create a property file");
            return null;
        }

        File propFile = new File(BASE_DIR + "/" + fileName);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(propFile));
            this.configList.put(propFile.getCanonicalPath(), writer);
            return propFile.getCanonicalPath();
        } catch (IOException e) {
            System.err.println("[ERROR] can't create property file (wrong path): " + fileName);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String close(String fileName) {
        File dFile = new File(BASE_DIR + "/" + fileName);
        try {
            if (this.configList.containsKey(dFile.getCanonicalPath())) {
                this.configList.get(dFile.getCanonicalPath()).close();
                this.configList.remove(dFile);
            }
            return dFile.getCanonicalPath();
        } catch (IOException e) {
            // file not opened
            return null;
        }
    }

    @Override
    public String delete(String fileName) {
        File dFile = new File(BASE_DIR + "/" + fileName);
        try {
            if (this.configList.containsKey(dFile.getCanonicalPath())) {
                this.configList.get(dFile.getCanonicalPath()).close();
                this.configList.remove(dFile);
                dFile.delete();
            }
            return dFile.getCanonicalPath();
        } catch (IOException e) {
            // file not opened
            return null;
        }
    }

    @Override
    public void closeAll() {
        Set<String> keys = this.configList.keySet();

        for (String key : keys) {
            try {
                this.configList.get(key).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.configList.clear();
    }


    /**
     * Add a configuration item
     * @param fileName configuration file
     * @param field field
     * @param value value
     */
    @Override
    public void addSingleConfig(String fileName, String field, String value) {
        File dFile = new File(BASE_DIR + "/" + fileName);

        try {
            if (this.configItem.containsKey(dFile.getCanonicalPath())) {
                this.configItem.get(dFile.getCanonicalPath()).addOneItem(field, value);
            } else {
                ConfigItem cItem = new ConfigItem();
                cItem.addOneItem(field, value);
                this.configItem.put(dFile.getCanonicalPath(), cItem);
            }
        } catch (IOException e) {
            System.err.println("[WARNING] can't get the property file: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Add multiple configuration items
     * @param fileName configuration file
     * @param fields fields
     * @param values values
     */
    @Override
    public void addMultipleConfig(String fileName, String[] fields, String[] values) {
        File dFile = new File(BASE_DIR + "/" + fileName);

        if (this.configItem.containsKey(dFile)) {
            try {
                this.configItem.get(dFile.getCanonicalPath()).addAllItem(fields, values);
            } catch (IOException e) {
                System.err.println("[WARNING] can't get the property file: " + fileName);
                e.printStackTrace();
            }
        } else {
            ConfigItem cItem = new ConfigItem();
            cItem.addAllItem(fields, values);
            try {
                this.configItem.put(dFile.getCanonicalPath(), cItem);
            } catch (IOException e) {
                System.err.println("[WARNING] can't get the property file: " + fileName);
                e.printStackTrace();
            }
        }

    }

    /*
    public static void main(String[] args) throws InterruptedException {
        ConfigWriter writer = new ConfigWriter();

        writer.setBaseDir("/Users/sindongboy/Documents/workspace/OMPConfiguration/config");

        writer.open("sample.properties");
        writer.open("sample2.properties");

        writer.addHeader("sample.properties", "This is Header\nThis is Header second line");

        writer.addSingleConfig("sample.properties", "DIR", "/Users/sindongboy/Documents/workspace/OMPConfiguration/resource");
        writer.addSingleConfig("sample.properties", "DIR2", "/Users/sindongboy/Documents/workspace/OMPConfiguration/resource");

        writer.write("sample.properties");
        //writer.clear("sample.properties");

        writer.close("sample.properties");
        writer.close("sample2.properties");

    }
    */


}
