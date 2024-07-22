package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static Properties properties = getProperties();


    public static String mailUsername = properties.getProperty("mail.auth.username");
    public static String mailPassword = properties.getProperty("mail.auth.password");

    public static Properties getProperties(){
        Properties props = new Properties();
        String propertiesFilename = "cred.properties";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(propertiesFilename)) {
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + propertiesFilename + " resource file: " + e);
        }
    }
}
