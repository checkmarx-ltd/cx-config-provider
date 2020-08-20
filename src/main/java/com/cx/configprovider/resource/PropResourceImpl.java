package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.*;

import javax.naming.ConfigurationException;
import java.util.Properties;

public class PropResourceImpl implements ConfigResource {
    
    protected ResourceType resourceType = ResourceType.PROPERTIES;
    Properties properties = new Properties();

    /**
     * loads a specific property
     * @param pathName full path to the property separated by . char
     * @param value value of the property
     */
    public void addPropertyPathValue(String pathName, String value){
        properties.put(pathName, value);
    }

    /**
     * loads a specific property
     * @param propertyName property name 
     * @param path full path to the property without property name separated by . char
     * @param value value of the property
     */
    public void addPropertyValue(String propertyName, String path, String value){
        properties.put(path + "." + propertyName, value);
    }

    /**
     * loads a set of properties. It is important that each key in the 
     * property file will contain a full path to the field in the final 
     * configuration object
     * @param properties a full set of properties to be converted into a configuration tree
     */
    public void loadProperties(Properties properties){
        properties.putAll(properties);
    }
    
    @Override
    public Config parse() throws ConfigurationException {
        return ConfigFactory.parseProperties(properties);
    }
}
