package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.naming.ConfigurationException;
import java.util.Properties;

public class EnvPropResourceImpl implements ConfigResource {

    Properties properties = new Properties();
    private ResourceType type = ResourceType.ENV_VARIABLES;

    public void addProperty(String key, String value){
        properties.put(key, value);
    }
    
    @Override
    public Config parse() throws ConfigurationException {
        return ConfigFactory.parseProperties(properties);
    }
}
