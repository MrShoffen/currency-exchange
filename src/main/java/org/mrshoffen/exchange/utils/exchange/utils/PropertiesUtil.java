package org.mrshoffen.exchange.utils.exchange.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {
    private static final String PROPERTIES_PATH = "properties/application.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH)) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can't load properties file!", e);
        }
    }
}
