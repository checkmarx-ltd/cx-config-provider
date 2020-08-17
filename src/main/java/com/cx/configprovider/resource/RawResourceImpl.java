package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class RawResourceImpl extends ConfigResourceImpl {
    private String content;
    private ResourceType type;
    private String  name;
    private Config config;
    
   
    public RawResourceImpl(ResourceType type, String fileContent, String name) throws ConfigurationException {
        this.type = type;
        this.content = fileContent;
        this.name = name;
    }
    
    @Override
    public Config parse() throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(content, "");
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(content);
        }
        
        return config;
    }

  
}
