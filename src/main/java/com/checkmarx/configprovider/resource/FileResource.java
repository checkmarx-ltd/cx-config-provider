package com.checkmarx.configprovider.resource;

import com.checkmarx.configprovider.dto.interfaces.ConfigResource;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.exceptions.ConfigProviderException;
import com.typesafe.config.Config;
import lombok.Getter;

import java.io.File;

/**
 * Contains resources of type Yml or Json stored in file system
 */

@Getter
public class FileResource extends AbstractFileResource implements ConfigResource {

    private File file;
    
    
    public FileResource(ResourceType type, String filepath) {
        file = new File(filepath);
        if(!file.exists()){
            throw new ConfigProviderException("File not found: " + filepath);
        }
        this.type = type;
        if(!type.equals(ResourceType.JSON) && !type.equals(ResourceType.YML)) {
            throw new UnsupportedOperationException();
        }
    }
    
  
    @Override
    Config load() {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(file);
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(file);
        }
        return config;
    }

    @Override
    public String getName() {
        return file.getPath();
    }
}
