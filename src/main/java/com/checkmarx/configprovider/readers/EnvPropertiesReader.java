package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.utility.PropertyLoader;


public class EnvPropertiesReader extends PropertiesReader implements ConfigReader {

    public EnvPropertiesReader(boolean loadEnvVariables)
    {
        resourceType = ResourceType.ENV_VARIABLES;

        if(loadEnvVariables) {
            properties = new PropertyLoader().loadEnvVariables();
        }
    }

    /**
     * loads environment variable named propertyName and places it in a specified
     * path in a configuration tree
     * @param propertyName environment variable named propertyName 
     * @param path in the configuration tree to place this property
     */
    public void addEnvVariable(String propertyName, String path){
        String propertyValue = System.getenv(propertyName);
        addPropertyValue(propertyName, path, propertyValue);
    }

    /**
     * loads system property named propertyName and places it in a specified
     * path in a configuration tree
     * @param propertyName system property named propertyName 
     * @param path in the configuration tree to place this property
     */
    public void addSystemProperty(String propertyName, String path){
        String propertyValue = System.getProperty(propertyName);
        addPropertyValue(propertyName, path, propertyValue);
    }
    

    
}
