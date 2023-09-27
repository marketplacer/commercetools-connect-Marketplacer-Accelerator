package com.commercetools.connect.marketplacer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final String PROPERTIES_FILE = "config.properties";
    public static final java.lang.String CLIENT_ID = "CTP_CLIENT_ID";
    public static final java.lang.String CLIENT_SECRET = "CTP_CLIENT_SECRET";
    public static final java.lang.String PROJECT_KEY = "CTP_PROJECT_KEY";
    public static final String MAIN_PRODUCT_TYPE = "mainProductType";
    public static final String ROOT_CATEGORY = "rootCategory";
    public static final String CHILD_CATEGORY = "childCategory";

    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientId() {
        String clientId = properties.getProperty(CLIENT_ID);
        if (null == clientId) {
            clientId = System.getenv(CLIENT_ID);
        }
        return clientId;
    }

    public String getClientSecret() {
        String clientSecret = properties.getProperty(CLIENT_SECRET);
        if (null == clientSecret) {
            clientSecret = System.getenv(CLIENT_SECRET);
        }
        return clientSecret;
    }

    public String getProjectKey() {
        String projectKey = properties.getProperty(PROJECT_KEY);
        if (null == projectKey) {
            projectKey = System.getenv(PROJECT_KEY);
        }
        return projectKey;
    }

    public String getMainProductType() {
        String mainProductType = properties.getProperty(MAIN_PRODUCT_TYPE);
        if (null == mainProductType) {
            mainProductType = System.getenv(MAIN_PRODUCT_TYPE);
        }
        return mainProductType;
    }

    public String getRootCategory() {
        String rootCategory = properties.getProperty(ROOT_CATEGORY);
        if (null == rootCategory) {
            rootCategory = System.getenv(ROOT_CATEGORY);
        }
        return rootCategory;
    }

    public String getChildCategory() {
        String childCategory = properties.getProperty(CHILD_CATEGORY);
        if (null == childCategory) {
            childCategory = System.getenv(CHILD_CATEGORY);
        }
        return childCategory;
    }
}
