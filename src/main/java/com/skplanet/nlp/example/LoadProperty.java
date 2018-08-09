package com.skplanet.nlp.example;


import com.skplanet.nlp.config.Configuration;

import java.io.IOException;
import java.net.URL;

/**
 * Shows how to use {@link Configuration} class<br>
 * please, refer to the documentation for the detail.<br>
 *
 * <br>Created by Donghun Shin<br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com<br>
 * Date: 6/18/13<br>
 */
public class LoadProperty {

    public static void main(String[] args) {

        //--------------------------------
        // configuration loading.
        //--------------------------------
        Configuration nConfig = Configuration.getInstance();
        try {
            nConfig.loadProperties("nlp_api.properties", Configuration.CLASSPATH_LOAD);
        } catch (IOException e) {
            System.out.println("can't find : nlp_api.properties");
            e.printStackTrace();
        }

        //--------------------------------
        // accessing properties
        //--------------------------------
        System.out.println("BASE_DIR for NLP: " + nConfig.getBaseDir());
        String[] fields = nConfig.propertyNames("nlp_api.properties");
        if (fields != null) {
            for (String f : fields) {
                System.out.println("field : " + f);
                System.out.println("value : " + nConfig.readProperty("nlp_api.properties", f) + "\n");
            }
        }

        //---------------------------------
        // get resource url
        //---------------------------------

        URL url = nConfig.getResource("1_sheet.exp", Configuration.CLASSPATH_LOAD);
        System.out.println(url.getPath());

    }
}
