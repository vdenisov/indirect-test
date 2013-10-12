package org.plukh.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public void setProperty(String property, String value) {
        properties.setProperty(property, value);
    }

    public void load(String fileName) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(fileName))) {
            properties.load(in);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
