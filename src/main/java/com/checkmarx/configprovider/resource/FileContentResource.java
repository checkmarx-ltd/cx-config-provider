package com.checkmarx.configprovider.resource;

import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import lombok.Getter;

import java.util.Optional;

/**
 * Contains String content of resources of type Yml or Json
 */
@Getter
public class FileContentResource extends AbstractFileResource implements ConfigResource {
    private String content;
    private String  name;

    public FileContentResource(ResourceType type, String fileContent, String name)  {
        this.type = type;
        this.content = fileContent;
        this.name = name;
    }

    public FileContentResource(String fileContent, String name) {
        if(isYml(name)){
            this.type = ResourceType.YML;
        }else{
            this.type = ResourceType.JSON;
        }
        
        this.content = fileContent;
        this.name = name;
    }

    public FileContentResource(String fileContent, String name, ResourceType type) {
        
        this.type = type;
        this.content = fileContent;
        this.name = name;
    }

    /**
     * Converts file String content of type Yml or Json to Config object.
     * Other types of files are not supported.
     * @return Config object
     * @throws com.checkmarx.configprovider.exceptions.ConfigProviderException exception when the string content of the resource
     * can not be converted to Config object
     */
    @Override
    public Config load() {
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
