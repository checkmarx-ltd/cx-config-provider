package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.*;

import javax.naming.ConfigurationException;
import java.util.Properties;

public class PropResourceImpl implements ConfigResource {
    
    protected ResourceType resourceType = ResourceType.PROPERTIES;
    Properties properties = new Properties();
    
    
    public void addPropertyValue(String propertyName, String xpath, String value){
        properties.put(xpath + "." + propertyName, value);
    }

    public void loadProperties(Properties properties){
        properties.putAll(properties);
    }
    
    @Override
    public Config parse() throws ConfigurationException {
        return ConfigFactory.parseProperties(properties);
    }
}
