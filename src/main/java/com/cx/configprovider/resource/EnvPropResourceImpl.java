package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import javax.naming.ConfigurationException;
import java.util.Properties;

public class EnvPropResourceImpl implements ConfigResource {

    private final Config config;
    Properties properties = new Properties();
    private ResourceType type = ResourceType.ENV_VARIABLES;

    public EnvPropResourceImpl(){
        config = ConfigFactory.empty();
    }
    public void addEnvVariable(String propertyName, String xpath){
        String propertyValue = System.getenv(propertyName);
        ConfigValue configValue = ConfigValueFactory.fromAnyRef(propertyValue);
        config.root().put(xpath, configValue);
    }

    public void addSystemProperty(String propertyName, String xpath){
        String propertyValue = System.getProperty(propertyName);
        ConfigValue configValue = ConfigValueFactory.fromAnyRef(propertyValue);
        config.root().put(xpath, configValue);
    }
    
    @Override
    public Config parse() throws ConfigurationException {
        return ConfigFactory.parseProperties(properties);
    }
}
