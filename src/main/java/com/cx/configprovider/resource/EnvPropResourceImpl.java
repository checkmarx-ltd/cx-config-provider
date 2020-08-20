package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;

public class EnvPropResourceImpl extends PropResourceImpl  {

    public EnvPropResourceImpl() {
        resourceType = ResourceType.ENV_VARIABLES;
    }

    /**
     * loads environment variable named propertyName and places it in a specified
     * path in a configuration tree
     * @param propertyName
     * @param xpath
     */
    public void addEnvVariable(String propertyName, String path){
        String propertyValue = System.getenv(propertyName);
        addPropertyValue(propertyName, path, propertyValue);
    }

    /**
     * loads system property named propertyName and places it in a specified
     * path in a configuration tree
     * @param propertyName
     * @param path
     */
    public void addSystemProperty(String propertyName, String path){
        String propertyValue = System.getProperty(propertyName);
        addPropertyValue(propertyName, path, propertyValue);
    }
    
}
