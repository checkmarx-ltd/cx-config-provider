package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.Optional;

/**
 * Contains resources of type Yml or Json stored in file system
 */

@Getter
public class FileResource extends AbstractFileResource implements ConfigResource {

    private File file;
    
    
    public FileResource(ResourceType type, String filepath) throws ConfigurationException {
        file = new File(filepath);
        if(!file.exists()){
            throw new ConfigurationException("File not found: " + filepath);
        }
        this.type = type;
    }
    
  
    @Override
    Config loadConfig() throws ConfigurationException {
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
