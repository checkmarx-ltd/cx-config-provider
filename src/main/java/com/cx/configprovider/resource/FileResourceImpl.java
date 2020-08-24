package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.Optional;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class FileResourceImpl extends ConfigResourceImpl {
;
    private File file;
    
    
    public FileResourceImpl(ResourceType type, String filepath) throws ConfigurationException {
        file = new File(filepath);
        if(!file.exists()){
            throw new ConfigurationException("File not found: " + filepath);
        }
        this.type = type;
    }
    
  
    @Override
    public Config parse() throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(file);
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(file);
        }
        return config;
    }

    @Override
    public String getName() {
        return Optional.ofNullable(file.getPath()).orElse(type.name()) ;
    }


}
