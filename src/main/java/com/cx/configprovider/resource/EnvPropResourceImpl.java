package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;

public class EnvPropResourceImpl extends PropResourceImpl  {

    public EnvPropResourceImpl() {
        resourceType = ResourceType.ENV_VARIABLES;
    }

    /**
     * loads environment variable named propertyName and places it in a specified
     * xpath in a configuration tree
     * @param propertyName
     * @param xpath
     */
    public void addEnvVariable(String propertyName, String xpath){
        String propertyValue = System.getenv(propertyName);
        addPropertyValue(propertyName, xpath, propertyValue);
    }

    /**
     * loads system property named propertyName and places it in a specified
     * xpath in a configuration tree
     * @param propertyName
     * @param xpath
     */
    public void addSystemProperty(String propertyName, String xpath){
        String propertyValue = System.getProperty(propertyName);
        addPropertyValue(propertyName, xpath, propertyValue);
    }
    
}
