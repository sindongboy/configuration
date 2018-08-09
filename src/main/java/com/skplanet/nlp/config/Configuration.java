package com.skplanet.nlp.config;


import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Simple Classpath based Configuration Class <br>
 * <br>
 * working flow.<br>
 *
 * 1. Get {@link Configuration} instance <br>
 * 2. Load Properties <br>
 * 3. Read Properties for given fields <br>
 * <br>
 * # You may write properties file using {@link com.skplanet.nlp.writer.ConfigWriter} class<br>
 * <br>
 * Created by Donghun Shin <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 5/19/13<br>
 */
public class Configuration implements Configurable{

    public static final int CLASSPATH_LOAD = 1;
    public static final int PHYSICALPATH_LOAD = 2;
    public static final int HDFSPATH_LOAD = 3;

    private static final String HDFS_PREFIX = "hdfs://";

    /**
     * Configuration Instance
     */
    private static Configuration instance = null;

    /**
     * Properties
     */
    private HashMap<String, Properties> properties = null;

    /**
     * Classpath for current configuration files
     */
    private String BASE_DIR = null;


    /**
     * Get an Instance of {@link Configuration}
     * @return instance of {@link Configuration}
     */
    public static Configuration getInstance() {
        if (instance == null) {
            // method synchronized
            synchronized (Configuration.class) {
                instance = new Configuration();
            }
        }
        return instance;
    }

    /**
     * prevent any other classes from instantiating
     */
    private Configuration() {
        this.properties = new HashMap<String, Properties>();
    }

    /**
     * Get classpath for current configuration files.
     * @return classpath for current configuration files.
     */
    public String getBaseDir() {
        return this.BASE_DIR;
    }

    /**
     * Get resource information including path and contents for the given resource file
     * @param fileName resource name
     * @return {@link URL} for the resource
     */
    @Override
    public URL getResource(String fileName) {
        return getResource(fileName, Configuration.CLASSPATH_LOAD);
    }

    @Override
    public URL getResource(String fileName, int mode) {
        URL url = null;
        switch (mode) {
            case Configuration.CLASSPATH_LOAD:
                fileName = fileName.replace("\"", "");
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }

                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }

                url = cl.getResource(fileName);
                break;
            case Configuration.PHYSICALPATH_LOAD:
                try {
                    url = new File(fileName).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
                break;
            case Configuration.HDFSPATH_LOAD:
                try {
                    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                    conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
                    conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
                    try {
                        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory(conf));
                    } catch (Error e) {

                    }
                    url = new URL(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                break;
        }

        return url;
    }

    /**
     * Get {@link URL} for resources.
     * @param fileNames resource names
     * @return array of {@link URL} for resources
     *
     */
    public URL[] getResources(String [] fileNames, int mode) {
        ArrayList<URL> res = new ArrayList<URL>();
        for (String f : fileNames) {
            URL url = getResource(f, mode);
            if (url != null) {
                res.add(url);
            }
        }

        if (res.size() > 0) {
            return res.toArray(new URL[res.size()]);
        } else {
            return null;
        }

    }

    /**
     * Get the {@link Properties}
     * @param fileName
     * @return properties object
     */
    public Properties getProperties(String fileName) {
        return this.properties.get(fileName);
    }

    /**
     * Load properties from the properties file specified by 'fileName'
     *
     * @param fileName Properties File Name
     *
     */
    @Override
    public void loadProperties(String fileName) throws IOException, NullPointerException {
        loadProperties(fileName, Configuration.CLASSPATH_LOAD);
    }

    @Override
    public void loadProperties(String fileName, int mode) throws IOException, NullPointerException {
        Properties prop = null;
        InputStream is = null;

        switch (mode) {
            // classpath based
            case Configuration.CLASSPATH_LOAD:
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }

                URL url = cl.getResource(fileName);
                if (url == null) {
                    throw new NullPointerException();
                }

                prop = new Properties();
                this.BASE_DIR = url.getPath();
                is = url.openStream();
                prop.load(is);
                is.close();
                break;
            // physical path based
            case Configuration.PHYSICALPATH_LOAD:
                this.BASE_DIR = fileName;
                prop = new Properties();
                prop.load(new BufferedReader(new FileReader(new File(fileName))));
                break;
            // hdfs path based
            case Configuration.HDFSPATH_LOAD:
                if(!fileName.startsWith(Configuration.HDFS_PREFIX)) {
                    System.err.println("[WARNING] invalid prefix of file path for hdfs : " + fileName);
                    throw new NullPointerException();
                }

                try {
                    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                    conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
                    conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
                    URI uri = new URI(fileName);
                    FileSystem hdfs = FileSystem.get(uri, conf);

                    try {
                        Path path = new Path(uri);
                        is = hdfs.open(path);
                        prop = new Properties();
                        this.BASE_DIR = uri.getPath();
                        prop.load(is);
                    } finally {
                        IOUtils.closeStream(is);
                    }
                } catch (Exception e) {
                    System.err.println("[WARNING] failure to initialize for hdfs : " + fileName);
                    e.printStackTrace();
                    throw new NullPointerException();
                }
                break;
        }

        this.properties.put(fileName, prop);
    }

    /**
     * Get the property value for the given field
     * @param propName property file name
     * @param field field name
     * @return value for a given property field
     */
    @Override
    public String readProperty(String propName, String field) {
        if (this.properties.containsKey(propName)) {
            String value;
            try {
                value = this.properties.get(propName).getProperty(field);
                return value;
            } catch (NullPointerException e) {
                System.err.println("[WARNING] no property exist : " + field);
                return null;
            }
        } else {
            System.err.println("[WARNING] no property is initialized");
        }
        return null;
    }


    /**
     * Get the fields list for a given {@link Properties} file
     * @param propName name of the properties file
     * @return array of the fields contained in the properties file.
     */
    @Override
    public String[] propertyNames(String propName) {
        ArrayList<String> rList = null;
        if (this.properties.containsKey(propName)) {
            Enumeration e = this.properties.get(propName).propertyNames();
            rList = new ArrayList<String>();
            while (e.hasMoreElements()) {
                rList.add((String)e.nextElement());
            }
        } else {
            System.err.println("[WARNING] no property is initialized");
            return null;
        }
        return rList.toArray(new String[rList.size()]);
    }

    /**
     * Get the fields list for a given {@link Properties} file
     * @param propName name of the properties file
     * @return list of the fields contained in the properties file.
     */
    public List<String> propertyNamesList(String propName) {
        ArrayList rList = null;
        if(!this.properties.containsKey(propName)) {
            System.err.println("[WARNING] no property is initialized");
            return null;
        } else {
            Enumeration e = ((Properties)this.properties.get(propName)).propertyNames();
            rList = new ArrayList();

            while(e.hasMoreElements()) {
                rList.add((String)e.nextElement());
            }

            return rList;
        }
    }

    /*
    public static void main(String[] args) throws IOException {

        Configuration config = Configuration.getInstance();
        String hdfs_file_path = "hdfs://UMi-hdn07:8020/user/usermodeling/nlp/config/nlp_api.properties";
        URL url = config.getResource(hdfs_file_path, Configuration.HDFSPATH_LOAD);
        System.out.println(url.toString());

        String propName = "keyterms.properties";
        String propPath = "/Users/sindongboy/Dropbox/Documents/workspace/sentiment-analyzer-core/config";


        config.loadProperties(propPath + "/" + propName, false);
        String btag = config.readProperty(propPath + "/" + propName, "BOUNDARY_TAG");
        System.out.println(btag);

        config.loadProperties(propName, true);
        btag = config.readProperty(propName, "BOUNDARY_TAG");
        System.out.println(btag);


        String BASE_DIR = "/Users/sindongboy/Documents/workspace/OMPConfiguration/config";
        String configFile1 = "sample.properties";
        String configFile2 = "sample2.properties";

        // Config. Write
        ConfigWriter cWriter = ConfigWriter.getInstance();

        cWriter.setBaseDir(BASE_DIR);

        cWriter.open(configFile1);
        cWriter.open(configFile2);

        cWriter.setHeader(configFile1, "#This is Header for " + configFile1);
        cWriter.addSingleConfig(configFile1, "DIR", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");
        cWriter.addSingleConfig(configFile1, "DIR2", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");
        cWriter.write(configFile1);

        cWriter.setHeader(configFile2, "#This is Header for " + configFile2);
        cWriter.addSingleConfig(configFile2, "DIR", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");
        cWriter.addSingleConfig(configFile2, "DIR2", "/Users/sindongboy/Documents/workspace/OMPConfiguration/config");
        cWriter.write(configFile2);

        cWriter.closeAll();

        Configuration config = Configuration.getInstance();
        config.loadProperties(configFile1);

        String[] list = config.propertyNames(configFile1);

        for (String l : list) {
            System.out.println(l + " -> " + config.readProperty(configFile1, l));
        }
    }
    */
}
