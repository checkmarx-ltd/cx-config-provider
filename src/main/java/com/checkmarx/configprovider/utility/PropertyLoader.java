package com.checkmarx.configprovider.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class PropertyLoader {

    private Properties props = null;
    private Properties envVariables = new Properties();


    private static final String MAIN_PROPERTIES_FILE = "config.properties";
    private static final String OVERRIDE_FILE = "config-secrets.properties";

    public void loadProperties() {
        Properties result = getProps(MAIN_PROPERTIES_FILE);

        Optional.ofNullable(getProps(OVERRIDE_FILE)).ifPresent(result::putAll);

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
        
        String envPropertyName = toEnvVariable(property);
        String envPropertyValue = System.getenv(envPropertyName);
        log.info(envPropertyName + " : " + envPropertyValue);

        //if system env variable is not defined, use local secrets file
        return StringUtils.isNotEmpty(envPropertyValue) ?
                envPropertyValue : props.getProperty(property);
    }

    public Properties loadEnvVariables(){
        
        Map<String, String> env = System.getenv();

        for ( Map.Entry<String, String> envVar : env.entrySet()) {
            String value = envVar.getValue();
            if(StringUtils.isNotEmpty(value)) {
                String parsedPropertyName = toConfigPath(envVar.getKey());
                envVariables.put(parsedPropertyName, value);
                log.info(envVar + " : " + value);
            }
        }
        
        return envVariables;
    }

    private String toConfigPath(String variableName) {
        return variableName.toLowerCase().replace("_", ".").trim();
    }

    private String toEnvVariable(String variableName) {
        return variableName.toUpperCase().replaceAll("\\.{1}", "_").trim();
    }
}
