package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.utility.PropertyLoader;

import java.util.Map;

public class EnvProperties extends PropertiesResource implements ConfigResource {

    public EnvProperties(boolean loadEnvVariables)
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
