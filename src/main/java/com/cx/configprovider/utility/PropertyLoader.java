package com.cx.configprovider.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class PropertyLoader {

    private Properties props = null;
    private Properties envVariables = new Properties();


    private static final String MAIN_PROPERTIES_FILE = "config.properties";
    private static final String OVERRIDE_FILE = "config-secrets.properties";

    public void loadProperties() {
        Properties result = getProps(MAIN_PROPERTIES_FILE);

        Properties overridingProps = getProps(OVERRIDE_FILE);
        result.putAll(overridingProps);

        props = result;
    }

    public String getFileUrlInClassloader(String filename) throws FileNotFoundException {
        
        ClassLoader classLoader = PropertyLoader.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            log.warn("File is not found in resources: {}", filename);
            throw new FileNotFoundException();
        }
        return resource.getFile();
    }
    
    private Properties getProps(String filename) {
        Properties properties = new Properties();
        ClassLoader classLoader = PropertyLoader.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            log.warn("Test property file is not found in resources: {}", filename);
        }
        else {
            try {
                properties.load(new FileReader(resource.getFile()));
            } catch (IOException e) {
                log.warn("Error reading resource: {}", filename);
            }
        }
        return properties;
    }

    public String getProperty(String property) {
        
        if(props == null){
            loadProperties();
        }
        
        String envPropertyName = normalize(property);
        String envPropertyValue = System.getenv(envPropertyName);
        log.info(envPropertyName + " : " + envPropertyValue);

        //if system env variable is not defined, use local secrets file
        return StringUtils.isNotEmpty(envPropertyValue) ?
                envPropertyValue : props.getProperty(property);
    }

    public Properties loadEnvVariables(){
        
        Map<String, String> env = System.getenv();

        for (String variableName : env.keySet()) {
            String value = env.get(variableName);
            if(StringUtils.isNotEmpty(value)) {
                String parsedPropertyName = normalize(variableName);
                envVariables.put(parsedPropertyName, env.get(variableName));
                log.info(variableName + " : " + value);
            }
        }
        
        return envVariables;
    }

    private String normalize(String variableName) {
        return variableName.toLowerCase().replaceAll("\\.", "_").trim();
    }
}
