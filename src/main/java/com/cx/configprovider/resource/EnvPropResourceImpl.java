package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import javax.naming.ConfigurationException;

public class EnvPropResourceImpl extends PropResourceImpl  {

    public EnvPropResourceImpl() {
        resourceType = ResourceType.ENV_VARIABLES;
    }
    
    public void addEnvVariable(String propertyName, String xpath){
        String propertyValue = System.getenv(propertyName);
        addPropertyValue(propertyName, xpath, propertyValue);
    }

    public void addSystemProperty(String propertyName, String xpath){
        String propertyValue = System.getProperty(propertyName);
        addPropertyValue(propertyName, xpath, propertyValue);
    }
    
}
