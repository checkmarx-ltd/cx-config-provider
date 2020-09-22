package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.EnumSet;
import java.util.Optional;

/**
 * Contains resources of type Yml or Json stored in file system
 */

@Getter
public class FileReader extends AbstractFileReader implements ConfigReader {

    private File file;
    
    
    public FileReader(ResourceType type, String filepath) throws ConfigurationException {
        file = new File(filepath);
        if(!file.exists()){
            throw new ConfigurationException("File not found: " + filepath);
        }
        this.type = type;
        if(!EnumSet.of(ResourceType.JSON, ResourceType.YAML).contains(type)) {
            throw new UnsupportedOperationException();
        }
    }
    
  
    @Override
    Config toConfig() throws ConfigurationException {
        if(ResourceType.YAML.equals(type)){
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
