package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.Optional;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class RawResourceImpl extends ConfigResourceImpl {
    private String content;
    private ResourceType type;
    private String  name;
    private Config config;

    private RawResourceImpl(){
        super();
    }
    
    public RawResourceImpl(ResourceType type, String fileContent, String name) throws ConfigurationException {
        this.type = type;
        this.content = fileContent;
        this.name = name;
    }

    public RawResourceImpl(String fileContent, String name) throws ConfigurationException {
        if(name.toUpperCase().endsWith(ResourceType.YML.toString().toUpperCase())){
            this.type = ResourceType.YML;
        }else{
            this.type = ResourceType.JSON;
        }
        
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

    @Override
    public String getName() {
        return Optional.ofNullable(name).orElse(type.name()) ;
    }


}
