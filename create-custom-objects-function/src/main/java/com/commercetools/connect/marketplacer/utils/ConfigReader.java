package com.commercetools.connect.marketplacer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader {

    private static final Logger logger = Logger.getLogger(ConfigReader.class.getName());
    private static final String PROPERTIES_FILE = "config.properties";
    public static final String CLIENT_ID = "CTP_CLIENT_ID";
    public static final String CLIENT_SECRET = "CTP_CLIENT_SECRET";
    public static final String PROJECT_KEY = "CTP_PROJECT_KEY";
    public static final String COMMERCETOOLS_CLIENT_REGION = "COMMERCETOOLS_CLIENT_REGION";

    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch(IOException e) {
            logger.log(Level.SEVERE, "Error while loading properties file!", e);
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

    public String getRegion() {
        String region = properties.getProperty(COMMERCETOOLS_CLIENT_REGION);
        if (null == region) {
            region = System.getenv(COMMERCETOOLS_CLIENT_REGION);
        }
        return region;
    }
}
